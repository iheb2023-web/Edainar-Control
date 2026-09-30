package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.dto.transfer.InternalTransferRequest;
import com.edinar.control.dto.transfer.InternalTransferResponse;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class InternalTransferService {

    private final CardMapper cardMapper;
    private final LedgerService ledgerService;
    private final CurrentUserService currentUserService;

    public InternalTransferService(
        CardMapper cardMapper,
        LedgerService ledgerService,
        CurrentUserService currentUserService
    ) {
        this.cardMapper = cardMapper;
        this.ledgerService = ledgerService;
        this.currentUserService = currentUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    public InternalTransferResponse transfer(InternalTransferRequest request) {
        if (request.getSourceCardId().equals(request.getDestinationCardId())) {
            throw new BadRequestException("Les cartes source et destination doivent être différentes");
        }

        BigDecimal fee = request.getFeeAmount() == null ? BigDecimal.ZERO : request.getFeeAmount();
        UUID userId = currentUserService.getCurrentUserId();
        Long firstId = Math.min(request.getSourceCardId(), request.getDestinationCardId());
        Long secondId = Math.max(request.getSourceCardId(), request.getDestinationCardId());

        cardMapper.findByIdAndUserIdForUpdate(firstId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable ou accès non autorisé : " + firstId));
        cardMapper.findByIdAndUserIdForUpdate(secondId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable ou accès non autorisé : " + secondId));

        UUID transferGroupId = UUID.randomUUID();
        CreateTransactionRequest sourceRequest = transactionRequest(
            request, "OUT", fee, "Transfert vers la carte " + request.getDestinationCardId());
        CreateTransactionRequest destinationRequest = transactionRequest(
            request, "IN", BigDecimal.ZERO, "Transfert depuis la carte " + request.getSourceCardId());

        var source = ledgerService.createTransactionWithGroup(
            request.getSourceCardId(), sourceRequest, transferGroupId);
        var destination = ledgerService.createTransactionWithGroup(
            request.getDestinationCardId(), destinationRequest, transferGroupId);

        return new InternalTransferResponse(source, destination, transferGroupId.toString());
    }

    private CreateTransactionRequest transactionRequest(
        InternalTransferRequest request, String direction, BigDecimal fee, String defaultDescription
    ) {
        CreateTransactionRequest transaction = new CreateTransactionRequest();
        transaction.setType("INTERNAL_TRANSFER");
        transaction.setDirection(direction);
        transaction.setAmount(request.getAmount());
        transaction.setFeeAmount(fee);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : defaultDescription);
        transaction.setReference(request.getReference());
        return transaction;
    }
}
