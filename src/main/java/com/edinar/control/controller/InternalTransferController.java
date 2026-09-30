package com.edinar.control.controller;

import com.edinar.control.dto.transfer.InternalTransferRequest;
import com.edinar.control.dto.transfer.InternalTransferResponse;
import com.edinar.control.service.InternalTransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
public class InternalTransferController {

    private final InternalTransferService internalTransferService;

    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @PostMapping("/internal-transfers")
    public ResponseEntity<InternalTransferResponse> transfer(
        @Valid @RequestBody InternalTransferRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(internalTransferService.transfer(request));
    }
}
