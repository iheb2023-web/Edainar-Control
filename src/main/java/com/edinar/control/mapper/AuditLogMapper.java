package com.edinar.control.mapper;

import com.edinar.control.domain.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper
public interface AuditLogMapper {

    int insert(AuditLog auditLog);

    List<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    List<AuditLog> findByUserFiltered(
        @Param("userId") UUID userId,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate,
        @Param("entityType") String entityType,
        @Param("entityId") Long entityId,
        @Param("action") String action,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
}
