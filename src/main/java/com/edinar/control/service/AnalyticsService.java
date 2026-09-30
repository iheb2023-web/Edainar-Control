package com.edinar.control.service;

import com.edinar.control.dto.analytics.AnalyticsResponse;
import com.edinar.control.mapper.AnalyticsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final AnalyticsMapper analyticsMapper;
    private final CurrentUserService currentUserService;

    public AnalyticsService(AnalyticsMapper analyticsMapper, CurrentUserService currentUserService) {
        this.analyticsMapper = analyticsMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse profit(OffsetDateTime from, OffsetDateTime to) {
        List<Map<String, Object>> series = analyticsMapper.profitByDay(currentUserService.getCurrentUserId(), from, to);
        return response(series, "net_profit_tnd", "gross_profit_tnd", "binance_fees_tnd", "other_fees_tnd");
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse expenses(OffsetDateTime from, OffsetDateTime to) {
        List<Map<String, Object>> series = analyticsMapper.expensesByDay(currentUserService.getCurrentUserId(), from, to);
        return response(series, "total_expenses", "atm_expenses", "online_expenses", "binance_fees_tnd");
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse cards(OffsetDateTime from, OffsetDateTime to) {
        List<Map<String, Object>> series = analyticsMapper.cards(currentUserService.getCurrentUserId(), from, to);
        return response(series, "current_balance", "total_in", "total_out");
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse volume(OffsetDateTime from, OffsetDateTime to) {
        List<Map<String, Object>> series = analyticsMapper.volumeByDay(currentUserService.getCurrentUserId(), from, to);
        return response(series, "volume_usdt", "bought_usdt", "sold_usdt", "binance_fees_usdt", "binance_fees_tnd");
    }

    private AnalyticsResponse response(List<Map<String, Object>> series, String... keys) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (String key : keys) {
            totals.put(key, series.stream().map(row -> number(row.get(key))).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return new AnalyticsResponse(totals, series);
    }

    private BigDecimal number(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal decimal) return decimal;
        return new BigDecimal(value.toString());
    }
}
