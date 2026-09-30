package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.LedgerTransaction;
import com.edinar.control.dto.card.CardResponse;
import com.edinar.control.dto.card.CreateCardRequest;
import com.edinar.control.dto.card.UpdateCardRequest;
import com.edinar.control.dto.card.UpdateCardStatusRequest;
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
public class CardService {

    private final CardMapper cardMapper;
    private final LedgerTransactionMapper ledgerTransactionMapper;
    private final CurrentUserService currentUserService;

    public CardService(
        CardMapper cardMapper,
        LedgerTransactionMapper ledgerTransactionMapper,
        CurrentUserService currentUserService
    ) {
        this.cardMapper = cardMapper;
        this.ledgerTransactionMapper = ledgerTransactionMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardResponse createCard(CreateCardRequest request) {
        UUID userId = currentUserService.getCurrentUserId();

        BigDecimal initialBalance = request.getInitialBalance() != null
            ? request.getInitialBalance()
            : BigDecimal.ZERO;

        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Le solde initial ne peut pas être négatif");
        }

        OffsetDateTime now = OffsetDateTime.now();

        Card card = new Card();
        card.setUserId(userId);
        card.setName(request.getName());
        card.setMaskedNumber(request.getMaskedNumber());
        card.setCardNumber(request.getCardNumber());
        card.setSecretCode(request.getSecretCode());
        card.setPhoneNumber(request.getPhoneNumber());
        card.setExpirationDate(request.getExpirationDate());
        card.setOwnerName(request.getOwnerName());
        card.setOwnerCin(request.getOwnerCin());
        card.setProvider(request.getProvider());
        card.setCurrency(request.getCurrency() != null && !request.getCurrency().isBlank()
            ? request.getCurrency().toUpperCase(java.util.Locale.ROOT) : "TND");
        card.setStatus("ACTIVE");
        card.setCurrentBalance(initialBalance);
        card.setLastVerifiedBalance(initialBalance);
        card.setLastVerifiedAt(now);
        card.setNotes(request.getNotes());
        card.setCreatedAt(now);
        card.setUpdatedAt(now);

        cardMapper.insert(card);

        // Enregistrer la transaction initiale dans le ledger pour traçabilité complète
        LedgerTransaction initialTx = new LedgerTransaction();
        initialTx.setCardId(card.getId());
        initialTx.setType("INITIAL_BALANCE");
        initialTx.setDirection("IN");
        initialTx.setAmount(initialBalance);
        initialTx.setFeeAmount(BigDecimal.ZERO);
        initialTx.setBalanceBefore(BigDecimal.ZERO);
        initialTx.setBalanceAfter(initialBalance);
        initialTx.setDescription("Solde initial - " + card.getName());
        initialTx.setReference("INIT-" + card.getId());
        initialTx.setTransactionDate(now);
        initialTx.setCreatedAt(now);
        initialTx.setUpdatedAt(now);

        ledgerTransactionMapper.insert(initialTx);

        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getAllCards() {
        UUID userId = currentUserService.getCurrentUserId();
        return cardMapper.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CardResponse getCardById(Long id) {
        Card card = cardMapper.findByIdAndUserId(id, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + id));
        return toResponse(card);
    }

    @Transactional(rollbackFor = Exception.class)
    public CardResponse updateCard(Long id, UpdateCardRequest request) {
        Card card = cardMapper.findByIdAndUserId(id, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + id));

        if (request.getName() == null
            && request.getMaskedNumber() == null
            && request.getCardNumber() == null
            && request.getSecretCode() == null
            && request.getPhoneNumber() == null
            && request.getExpirationDate() == null
            && request.getOwnerName() == null
            && request.getOwnerCin() == null
            && request.getProvider() == null
            && request.getNotes() == null) {
            throw new BadRequestException("Au moins un champ doit être fourni pour modifier la carte");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (request.getName() != null) {
            card.setName(requireNonBlank(request.getName(), "Le nom de la carte ne peut pas être vide"));
        }
        if (request.getMaskedNumber() != null) {
            card.setMaskedNumber(request.getMaskedNumber());
        }
        if (request.getCardNumber() != null) card.setCardNumber(request.getCardNumber());
        if (request.getSecretCode() != null) card.setSecretCode(request.getSecretCode());
        if (request.getPhoneNumber() != null) card.setPhoneNumber(request.getPhoneNumber());
        if (request.getExpirationDate() != null) card.setExpirationDate(request.getExpirationDate());
        if (request.getOwnerName() != null) card.setOwnerName(request.getOwnerName());
        if (request.getOwnerCin() != null) card.setOwnerCin(request.getOwnerCin());
        if (request.getProvider() != null) {
            card.setProvider(requireNonBlank(request.getProvider(), "Le fournisseur ne peut pas être vide"));
        }
        if (request.getNotes() != null) {
            card.setNotes(request.getNotes());
        }
        card.setUpdatedAt(now);

        cardMapper.update(card);
        return toResponse(card);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCard(Long id) {
        Card card = cardMapper.findByIdAndUserId(id, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + id));
        if (!ledgerTransactionMapper.findByCardId(card.getId()).isEmpty()) {
            throw new BadRequestException("Cette carte possède un historique et ne peut pas être supprimée");
        }
        cardMapper.delete(id);
    }

    private String requireNonBlank(String value, String message) {
        if (value.isBlank()) {
            throw new BadRequestException(message);
        }
        return value;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardResponse updateCardStatus(Long id, UpdateCardStatusRequest request) {
        Card card = cardMapper.findByIdAndUserId(id, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + id));

        OffsetDateTime now = OffsetDateTime.now();
        card.setStatus(request.getStatus());
        card.setUpdatedAt(now);

        cardMapper.updateStatus(id, request.getStatus(), now);
        return toResponse(card);
    }

    private CardResponse toResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setUserId(card.getUserId());
        response.setName(card.getName());
        response.setMaskedNumber(card.getMaskedNumber());
        response.setLastFourDigits(card.getCardNumber() != null && card.getCardNumber().length() >= 4
            ? card.getCardNumber().substring(card.getCardNumber().length() - 4) : card.getMaskedNumber());
        response.setPhoneNumber(card.getPhoneNumber());
        response.setExpirationDate(card.getExpirationDate());
        response.setOwnerName(card.getOwnerName());
        response.setCardNumber(card.getCardNumber());
        response.setSecretCode(card.getSecretCode());
        response.setOwnerCin(card.getOwnerCin());
        response.setProvider(card.getProvider());
        response.setCurrency(card.getCurrency());
        response.setStatus(card.getStatus());
        response.setCurrentBalance(card.getCurrentBalance());
        response.setLastVerifiedBalance(card.getLastVerifiedBalance());
        response.setLastVerifiedAt(card.getLastVerifiedAt());
        response.setNotes(card.getNotes());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
    }
}
