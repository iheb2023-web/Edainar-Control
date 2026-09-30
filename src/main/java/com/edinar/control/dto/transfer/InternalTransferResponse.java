package com.edinar.control.dto.transfer;

import com.edinar.control.dto.transaction.TransactionResponse;

public class InternalTransferResponse {
    private TransactionResponse sourceTransaction;
    private TransactionResponse destinationTransaction;
    private String transferGroupId;

    public InternalTransferResponse() {}

    public InternalTransferResponse(TransactionResponse src, TransactionResponse dst, String groupId) {
        this.sourceTransaction = src;
        this.destinationTransaction = dst;
        this.transferGroupId = groupId;
    }

    public TransactionResponse getSourceTransaction() { return sourceTransaction; }
    public void setSourceTransaction(TransactionResponse sourceTransaction) { this.sourceTransaction = sourceTransaction; }
    public TransactionResponse getDestinationTransaction() { return destinationTransaction; }
    public void setDestinationTransaction(TransactionResponse destinationTransaction) { this.destinationTransaction = destinationTransaction; }
    public String getTransferGroupId() { return transferGroupId; }
    public void setTransferGroupId(String transferGroupId) { this.transferGroupId = transferGroupId; }
}
