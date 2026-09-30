package com.edinar.control.dto.analytics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    private Map<String, BigDecimal> totals;
    private List<Map<String, Object>> series;

    public AnalyticsResponse() {}
    public AnalyticsResponse(Map<String, BigDecimal> totals, List<Map<String, Object>> series) {
        this.totals = totals;
        this.series = series;
    }
    public Map<String, BigDecimal> getTotals() { return totals; }
    public void setTotals(Map<String, BigDecimal> totals) { this.totals = totals; }
    public List<Map<String, Object>> getSeries() { return series; }
    public void setSeries(List<Map<String, Object>> series) { this.series = series; }
}
