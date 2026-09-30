package com.edinar.control.service;

import com.edinar.control.domain.FeeRule;
import com.edinar.control.mapper.FeeRuleMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeRuleServiceTest {
    private final FeeRuleService service = new FeeRuleService((FeeRuleMapper) null);

    @Test
    void percentageFeeUsesEightDecimalPlacesAndConversionRoundsToTnd() {
        FeeRule rule = rule("PERCENTAGE", "0.2");
        BigDecimal fee = service.calculateFeeUsdt(rule, new BigDecimal("123.45678901"));
        assertEquals(new BigDecimal("0.24691358"), fee);
        assertEquals(new BigDecimal("0.309"), service.convertUsdtToTnd(fee, new BigDecimal("1.25")));
    }

    @Test
    void fixedFeeHonorsMinimumAndMaximum() {
        FeeRule rule = rule("FIXED", "2");
        rule.setMinFee(new BigDecimal("3"));
        rule.setMaxFee(new BigDecimal("4"));
        assertEquals(new BigDecimal("3.00000000"), service.calculateFeeUsdt(rule, BigDecimal.ONE));
        rule.setFeeValue(new BigDecimal("8"));
        assertEquals(new BigDecimal("4.00000000"), service.calculateFeeUsdt(rule, BigDecimal.ONE));
    }

    private FeeRule rule(String type, String value) {
        FeeRule rule = new FeeRule();
        rule.setFeeType(type);
        rule.setFeeValue(new BigDecimal(value));
        return rule;
    }
}
