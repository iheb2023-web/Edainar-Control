package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.LedgerTransaction;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.dto.transaction.TransactionPageResponse;
import com.edinar.control.dto.transaction.TransactionResponse;
import com.edinar.control.dto.transaction.VoidTransactionRequest;
import com.edinar.control.dto.transaction.VoidTransactionsPeriodRequest;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerService {

    private final LedgerTransactionMapper ledgerTransactionMapper;
    private final CardMapper cardMapper;
    private final LimitService limitService;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;

    public LedgerService(
        LedgerTransactionMapper ledgerTransactionMapper,
        CardMapper cardMapper,
        LimitService limitService,
        AuditLogService auditLogService,
        CurrentUserService currentUserService
    ) {
        this.ledgerTransactionMapper = ledgerTransactionMapper;
        this.cardMapper = cardMapper;
        this.limitService = limitService;
        this.auditLogService = auditLogService;
        this.currentUserService = currentUserService;
    }

    // =========================================================================
    // Création de transaction — pipeline complet
    // =========================================================================

    /**
     * Crée une transaction financière de manière atomique et sécurisée.
     *
     * Pipeline :
     * 1. Vérification ownership carte (user_id = currentUser)
     * 2. SELECT FOR UPDATE — verrouillage ligne carte (concurrence)
     * 3. Vérification statut carte
     * 4. Résolution direction IN/OUT
     * 5. Validation limites (LimitService)
     * 6. Calcul balance_before / balance_after
     * 7. Vérification solde suffisant (pour OUT)
     * 8. INSERT ledger_transactions
     * 9. UPDATE cards.current_balance
     * 10. Audit log
     * → COMMIT (ou ROLLBACK si une étape échoue)
     */
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse createTransaction(Long cardId, CreateTransactionRequest request) {
        UUID userId = currentUserService.getCurrentUserId();

        // 1+2. Ownership + SELECT FOR UPDATE — atomique
        Card card = cardMapper.findByIdAndUserIdForUpdate(cardId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carte introuvable ou accès non autorisé : " + cardId));

        // 3. Statut carte
        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new BadRequestException(
                "Impossible d'effectuer une transaction : la carte est " + card.getStatus());
        }

        OffsetDateTime txDate = request.getTransactionDate() != null
            ? request.getTransactionDate()
            : OffsetDateTime.now();

        // 4. Direction
        String direction = resolveDirection(request.getType(), request.getDirection());

        // 5. Limites
        limitService.validateTransactionLimits(cardId, request.getType(), request.getAmount(), txDate);

        // 6. Frais et soldes
        BigDecimal fee = request.getFeeAmount() != null ? request.getFeeAmount() : BigDecimal.ZERO;
        BigDecimal amount = request.getAmount();
        BigDecimal balanceBefore = card.getCurrentBalance();
        BigDecimal balanceAfter;

        if ("OUT".equalsIgnoreCase(direction)) {
            BigDecimal totalDebit = amount.add(fee);
            if (balanceBefore.compareTo(totalDebit) < 0) {
                throw new BadRequestException(String.format(
                    "Solde insuffisant : disponible %s TND, débit requis %s TND (montant: %s + frais: %s)",
                    balanceBefore.toPlainString(), totalDebit.toPlainString(),
                    amount.toPlainString(), fee.toPlainString()
                ));
            }
            balanceAfter = balanceBefore.subtract(totalDebit);
        } else {
            balanceAfter = balanceBefore.add(amount).subtract(fee);
            if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Le solde après opération ne peut pas être négatif");
            }
        }

        OffsetDateTime now = OffsetDateTime.now();

        // 8. INSERT ledger_transactions
        LedgerTransaction tx = new LedgerTransaction();
        tx.setCardId(cardId);
        tx.setType(request.getType().toUpperCase());
        tx.setDirection(direction);
        tx.setAmount(amount);
        tx.setFeeAmount(fee);
        tx.setBalanceBefore(balanceBefore);
        tx.setBalanceAfter(balanceAfter);
        tx.setCategoryId(request.getCategoryId());
        tx.setDescription(request.getDescription());
        tx.setReference(request.getReference());
        tx.setTransactionDate(txDate);
        tx.setIsVoid(false);
        tx.setCreatedAt(now);
        tx.setUpdatedAt(now);

        ledgerTransactionMapper.insert(tx);

        // 9. UPDATE cards.current_balance
        cardMapper.updateBalance(cardId, balanceAfter, now);

        // 10. Audit log
        auditLogService.log(
            card.getUserId(), "TRANSACTION", tx.getId(), "CREATE",
            null, tx,
            "Transaction " + tx.getType() + " sur la carte " + card.getName()
        );

        return toResponse(tx);
    }

    /**
     * Variante interne pour les transferts — accepte un transferGroupId pré-généré.
     */
    @Transactional(
        propagation = org.springframework.transaction.annotation.Propagation.MANDATORY,
        rollbackFor = Exception.class
    )
    public TransactionResponse createTransactionWithGroup(
        Long cardId, CreateTransactionRequest request, UUID transferGroupId
    ) {
        // Les verrous sont déjà acquis par InternalTransferService dans l'ordre déterministe
        Card card = cardMapper.findByIdForUpdate(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable : " + cardId));

        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new BadRequestException("Carte " + card.getName() + " est " + card.getStatus());
        }

        OffsetDateTime txDate = request.getTransactionDate() != null
            ? request.getTransactionDate() : OffsetDateTime.now();
        String direction = resolveDirection(request.getType(), request.getDirection());

        BigDecimal fee = request.getFeeAmount() != null ? request.getFeeAmount() : BigDecimal.ZERO;
        BigDecimal amount = request.getAmount();
        BigDecimal balanceBefore = card.getCurrentBalance();
        BigDecimal balanceAfter;

        if ("OUT".equalsIgnoreCase(direction)) {
            BigDecimal total = amount.add(fee);
            if (balanceBefore.compareTo(total) < 0) {
                throw new BadRequestException(String.format(
                    "Solde insuffisant sur '%s' : disponible %s TND, requis %s TND",
                    card.getName(), balanceBefore.toPlainString(), total.toPlainString()));
            }
            balanceAfter = balanceBefore.subtract(total);
        } else {
            balanceAfter = balanceBefore.add(amount).subtract(fee);
        }

        OffsetDateTime now = OffsetDateTime.now();
        LedgerTransaction tx = new LedgerTransaction();
        tx.setCardId(cardId);
        tx.setType(request.getType().toUpperCase());
        tx.setDirection(direction);
        tx.setAmount(amount);
        tx.setFeeAmount(fee);
        tx.setBalanceBefore(balanceBefore);
        tx.setBalanceAfter(balanceAfter);
        tx.setCategoryId(request.getCategoryId());
        tx.setDescription(request.getDescription());
        tx.setReference(request.getReference());
        tx.setTransactionDate(txDate);
        tx.setTransferGroupId(transferGroupId);
        tx.setIsVoid(false);
        tx.setCreatedAt(now);
        tx.setUpdatedAt(now);

        ledgerTransactionMapper.insert(tx);
        cardMapper.updateBalance(cardId, balanceAfter, now);

        auditLogService.log(card.getUserId(), "TRANSACTION", tx.getId(), "CREATE",
            null, tx, "Leg transfert interne " + tx.getType() + " carte " + card.getName());

        return toResponse(tx);
    }

    // =========================================================================
    // VOID — annulation sans suppression physique
    // =========================================================================

    /**
     * Annule une transaction financière.
     *
     * Pipeline :
     * 1. Vérifier l'ownership de la transaction (via la carte)
     * 2. Vérifier que la transaction n'est pas déjà annulée
     * 3. Marquer is_void = TRUE (jamais DELETE)
     * 4. Créer une transaction compensatoire pour rétablir le solde
     * 5. Audit log
     */
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse voidTransaction(Long txId, VoidTransactionRequest request) {
        UUID userId = currentUserService.getCurrentUserId();

        LedgerTransaction tx = ledgerTransactionMapper.findByIdForUpdate(txId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Transaction introuvable : " + txId));

        // Ownership via la carte
        cardMapper.findByIdAndUserId(tx.getCardId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Accès non autorisé à cette transaction"));

        if (Boolean.TRUE.equals(tx.getIsVoid())) {
            throw new BadRequestException("Cette transaction est déjà annulée");
        }

        OffsetDateTime now = OffsetDateTime.now();

        // 1. Marquer is_void = TRUE
        ledgerTransactionMapper.voidTransaction(txId, request.getReason(), now, userId);

        // 2. Lire la carte avec verrou pour créer la compensation
        Card card = cardMapper.findByIdForUpdate(tx.getCardId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable"));

        // 3. Compensation : inverser l'effet sur le solde
        BigDecimal compensation;
        String compensationDirection;
        String compensationDescription = "Annulation de transaction #" + txId + " — " + request.getReason();

        if ("OUT".equalsIgnoreCase(tx.getDirection())) {
            // L'original a débité → on recrédite
            compensation = tx.getAmount().add(tx.getFeeAmount() != null ? tx.getFeeAmount() : BigDecimal.ZERO);
            compensationDirection = "IN";
        } else {
            // L'original a crédité → on redébite
            compensation = tx.getAmount();
            compensationDirection = "OUT";
        }

        BigDecimal newBalance = compensationDirection.equals("IN")
            ? card.getCurrentBalance().add(compensation)
            : card.getCurrentBalance().subtract(compensation);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("L'annulation rendrait le solde négatif");
        }

        LedgerTransaction compensationTx = new LedgerTransaction();
        compensationTx.setCardId(tx.getCardId());
        compensationTx.setType("ADJUSTMENT");
        compensationTx.setDirection(compensationDirection);
        compensationTx.setAmount(compensation);
        compensationTx.setFeeAmount(BigDecimal.ZERO);
        compensationTx.setBalanceBefore(card.getCurrentBalance());
        compensationTx.setBalanceAfter(newBalance);
        compensationTx.setDescription(compensationDescription);
        compensationTx.setReference("VOID-" + txId);
        compensationTx.setTransactionDate(now);
        compensationTx.setIsVoid(false);
        compensationTx.setCreatedAt(now);
        compensationTx.setUpdatedAt(now);

        ledgerTransactionMapper.insert(compensationTx);
        cardMapper.updateBalance(tx.getCardId(), newBalance, now);

        // 4. Audit
        auditLogService.log(userId, "TRANSACTION", txId, "VOID",
            tx, "Transaction annulée — " + request.getReason(),
            request.getReason());

        tx.setIsVoid(true);
        tx.setVoidReason(request.getReason());
        tx.setVoidedAt(now);
        tx.setVoidedBy(userId);
        return toResponse(tx);
    }

    // =========================================================================
    // Lecture
    // =========================================================================

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCard(Long cardId) {
        verifyCardOwnership(cardId);
        return ledgerTransactionMapper.findByCardId(cardId).stream()
            .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransactionPageResponse getTransactionPage(
        Long cardId, OffsetDateTime from, OffsetDateTime to, int page, int size
    ) {
        verifyCardOwnership(cardId);
        int offset = page * size;
        List<TransactionResponse> content = ledgerTransactionMapper
            .findByCardIdPaginated(cardId, null, from, to, false, offset, size)
            .stream().map(this::toResponse).toList();
        long totalElements = ledgerTransactionMapper.countByCardIdFiltered(cardId, null, from, to, false);
        return new TransactionPageResponse(content, page, size, totalElements);
    }

    @Transactional(rollbackFor = Exception.class)
    public int voidTransactionsByPeriod(Long cardId, VoidTransactionsPeriodRequest request) {
        verifyCardOwnership(cardId);
        if (request.getFrom().isAfter(request.getTo())) {
            throw new BadRequestException("La date de début doit précéder la date de fin");
        }

        List<LedgerTransaction> transactions = ledgerTransactionMapper.findByCardIdBetween(
            cardId, request.getFrom(), request.getTo());
        VoidTransactionRequest voidRequest = new VoidTransactionRequest();
        voidRequest.setReason(request.getReason());
        for (LedgerTransaction transaction : transactions) {
            voidTransaction(transaction.getId(), voidRequest);
        }
        return transactions.size();
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteTransactionsByPeriod(Long cardId, VoidTransactionsPeriodRequest request) {
        verifyCardOwnership(cardId);
        if (request.getFrom().isAfter(request.getTo())) {
            throw new BadRequestException("La date de début doit précéder la date de fin");
        }
        return ledgerTransactionMapper.deleteByCardIdBetween(cardId, request.getFrom(), request.getTo());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCardPaginated(
        Long cardId, String type, OffsetDateTime from, OffsetDateTime to,
        Boolean isVoid, int page, int size
    ) {
        verifyCardOwnership(cardId);
        int offset = page * size;
        return ledgerTransactionMapper.findByCardIdPaginated(cardId, type, from, to, isVoid, offset, size)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countTransactions(Long cardId, String type, OffsetDateTime from, OffsetDateTime to, Boolean isVoid) {
        verifyCardOwnership(cardId);
        return ledgerTransactionMapper.countByCardIdFiltered(cardId, type, from, to, isVoid);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(Long cardId, int limit) {
        verifyCardOwnership(cardId);
        return ledgerTransactionMapper.findRecentByCardId(cardId, Math.min(limit, 100))
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        UUID userId = currentUserService.getCurrentUserId();
        LedgerTransaction tx = ledgerTransactionMapper.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Transaction introuvable : " + id));

        // Ownership via carte
        cardMapper.findByIdAndUserId(tx.getCardId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException("Accès non autorisé"));

        return toResponse(tx);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void verifyCardOwnership(Long cardId) {
        UUID userId = currentUserService.getCurrentUserId();
        cardMapper.findByIdAndUserId(cardId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carte introuvable ou accès non autorisé : " + cardId));
    }

    private String resolveDirection(String type, String requestedDirection) {
        if (requestedDirection != null && !requestedDirection.isBlank()) {
            String dir = requestedDirection.trim().toUpperCase();
            if ("IN".equals(dir) || "OUT".equals(dir)) return dir;
            throw new BadRequestException("La direction doit être 'IN' ou 'OUT'");
        }

        return switch (type.trim().toUpperCase()) {
            case "RECEIVE", "P2P_SELL", "INITIAL_BALANCE" -> "IN";
            case "SEND", "ATM_WITHDRAWAL", "ONLINE_PAYMENT", "CARD_PAYMENT", "PHONE_RECHARGE",
                 "P2P_BUY", "FEE", "INTERNAL_TRANSFER", "ADJUSTMENT" -> "OUT";
            default -> throw new BadRequestException("Type de transaction inconnu : " + type);
        };
    }

    public TransactionResponse toResponse(LedgerTransaction tx) {
        TransactionResponse r = new TransactionResponse();
        r.setId(tx.getId());
        r.setCardId(tx.getCardId());
        r.setType(tx.getType());
        r.setDirection(tx.getDirection());
        r.setAmount(tx.getAmount());
        r.setFeeAmount(tx.getFeeAmount());
        r.setBalanceBefore(tx.getBalanceBefore());
        r.setBalanceAfter(tx.getBalanceAfter());
        r.setCategoryId(tx.getCategoryId());
        r.setDescription(tx.getDescription());
        r.setReference(tx.getReference());
        r.setTransactionDate(tx.getTransactionDate());
        r.setCreatedAt(tx.getCreatedAt());
        r.setUpdatedAt(tx.getUpdatedAt());
        r.setIsVoid(tx.getIsVoid());
        r.setVoidReason(tx.getVoidReason());
        r.setVoidedAt(tx.getVoidedAt());
        r.setVoidedBy(tx.getVoidedBy());
        return r;
    }
}
