package com.edinar.control.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface AnalyticsMapper {
    List<Map<String, Object>> profitByDay(@Param("userId") UUID userId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate);
    List<Map<String, Object>> expensesByDay(@Param("userId") UUID userId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate);
    List<Map<String, Object>> cards(@Param("userId") UUID userId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate);
    List<Map<String, Object>> volumeByDay(@Param("userId") UUID userId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate);
}
