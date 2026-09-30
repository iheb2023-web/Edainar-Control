package com.edinar.control.service;

import com.edinar.control.domain.AuditLog;
import com.edinar.control.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AuditQueryService {
    private final AuditLogMapper auditLogMapper;
    private final CurrentUserService currentUserService;

    public AuditQueryService(AuditLogMapper auditLogMapper, CurrentUserService currentUserService) {
        this.auditLogMapper = auditLogMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<AuditLog> find(OffsetDateTime fromDate, OffsetDateTime toDate,
                               String entityType, Long entityId, String action,
                               int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page doit être >= 0 et size compris entre 1 et 100");
        }
        return auditLogMapper.findByUserFiltered(
            currentUserService.getCurrentUserId(), fromDate, toDate,
            entityType, entityId, action, page * size, size);
    }
}