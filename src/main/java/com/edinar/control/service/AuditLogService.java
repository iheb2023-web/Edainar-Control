package com.edinar.control.service;

import com.edinar.control.domain.AuditLog;
import com.edinar.control.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void log(
        UUID userId,
        String entityType,
        Long entityId,
        String action,
        Object oldData,
        Object newData,
        String reason
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setOldData(toJson(oldData));
        auditLog.setNewData(toJson(newData));
        auditLog.setReason(reason);
        auditLog.setRequestId(UUID.randomUUID());
        auditLog.setCreatedAt(OffsetDateTime.now());

        auditLogMapper.insert(auditLog);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        if (obj instanceof String str) {
            try {
                return objectMapper.writeValueAsString(str);
            } catch (Exception e) {
                return "\"\"";
            }
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
