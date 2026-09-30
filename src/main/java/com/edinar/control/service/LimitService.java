package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.LimitRule;
import com.edinar.control.dto.limit.CardLimitsResponse;
import com.edinar.control.dto.limit.CreateLimitRuleRequest;
import com.edinar.control.dto.limit.LimitUsageDto;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import com.edinar.control.mapper.LimitRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class LimitService {

    private final LimitRuleMapper limitRuleMapper;
    private final LedgerTransactionMapper ledgerTransactionMapper;
    private final CardMapper cardMapper;
    private final CurrentUserService currentUserService;

    public LimitService(
        LimitRuleMapper limitRuleMapper,
        LedgerTransactionMapper ledgerTransactionMapper,
        CardMapper cardMapper,
        CurrentUserService currentUserService
    ) {
        this.limitRuleMapper = limitRuleMapper;
        this.ledgerTransactionMapper = ledgerTransactionMapper;
        this.cardMapper = cardMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardLimitsResponse getCardLimits(Long cardId) {
        Card card = cardMapper.findByIdAndUserId(cardId, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + cardId));

        List<LimitRule> rules = limitRuleMapper.findActiveByCardId(cardId);
        if (rules.isEmpty()) {
            initDefaultLimits(cardId);
            rules = limitRuleMapper.findActiveByCardId(cardId);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Africa/Tunis"));
        List<LimitUsageDto> usageList = new ArrayList<>();

        for (LimitRule rule : rules) {
            usageList.add(calculateUsage(cardId, rule, now));
        }

        return new CardLimitsResponse(card.getId(), card.getName(), usageList);
    }

    @Transactional(readOnly = true)
    public void validateTransactionLimits(
        Long cardId,
        String operationType,
        BigDecimal amount,
        OffsetDateTime txDate
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        OffsetDateTime date = txDate != null ? txDate : OffsetDateTime.now(ZoneId.of("Africa/Tunis"));
        String limitOperation = resolveLimitOperation(operationType);
        List<LimitRule> rules = limitRuleMapper.findActiveByCardId(cardId);

        for (LimitRule rule : rules) {
            if (rule.getOperationType().equalsIgnoreCase(limitOperation)) {
                OffsetDateTime startDate = effectiveStartDate(rule, date);
                OffsetDateTime endDate = calculateEndDate(rule.getPeriodType(), date);

                BigDecimal currentSpent = ledgerTransactionMapper.sumAmountByCardAndDirectionBetween(
                    cardId,
                    "SEND".equalsIgnoreCase(rule.getOperationType()) ? "OUT" : "IN",
                    startDate,
                    endDate
                );

                if (currentSpent == null) {
                    currentSpent = BigDecimal.ZERO;
                }

                BigDecimal projected = currentSpent.add(amount);
                if (projected.compareTo(rule.getLimitAmount()) > 0) {
                    throw new BadRequestException(String.format(
                        "Dépassement du plafond %s %s : total prévu %s TND > plafond %s TND (actuel : %s TND)",
                        rule.getOperationType(),
                        rule.getPeriodType(),
                        projected,
                        rule.getLimitAmount(),
                        currentSpent
                    ));
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void createLimitRule(Long cardId, CreateLimitRuleRequest request) {
        cardMapper.findByIdAndUserId(cardId, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + cardId));

        OffsetDateTime now = OffsetDateTime.now();
        LimitRule rule = new LimitRule();
        rule.setCardId(cardId);
        rule.setOperationType(request.getOperationType().toUpperCase());
        rule.setPeriodType(request.getPeriodType().toUpperCase());
        rule.setLimitAmount(request.getLimitAmount());
        rule.setWarningPercentage(request.getWarningPercentage() != null ? request.getWarningPercentage() : new BigDecimal("80.00"));
        rule.setCriticalPercentage(request.getCriticalPercentage() != null ? request.getCriticalPercentage() : new BigDecimal("90.00"));
        rule.setEffectiveFrom(now);
        rule.setEffectiveTo(null);
        rule.setIsActive(true);
        rule.setCreatedAt(now);

        limitRuleMapper.insert(rule);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLimitRule(Long cardId, String operationType, String periodType, CreateLimitRuleRequest request) {
        cardMapper.findByIdAndUserId(cardId, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + cardId));

        LimitRule current = limitRuleMapper.findActiveByCardIdAndOpAndPeriod(
            cardId, operationType.toUpperCase(), periodType.toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Règle de limite introuvable"));
        OffsetDateTime now = OffsetDateTime.now();
        limitRuleMapper.deactivate(current.getId(), now);
        createLimitRule(cardId, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetLimitRules(Long cardId) {
        cardMapper.findByIdAndUserId(cardId, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable avec l'identifiant : " + cardId));

        List<LimitRule> rules = limitRuleMapper.findActiveByCardId(cardId);
        if (rules.isEmpty()) {
            initDefaultLimits(cardId);
            rules = limitRuleMapper.findActiveByCardId(cardId);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Africa/Tunis"));
        for (LimitRule current : rules) {
            limitRuleMapper.deactivate(current.getId(), now);
            LimitRule resetRule = new LimitRule();
            resetRule.setCardId(cardId);
            resetRule.setOperationType(current.getOperationType());
            resetRule.setPeriodType(current.getPeriodType());
            resetRule.setLimitAmount(current.getLimitAmount());
            resetRule.setWarningPercentage(current.getWarningPercentage());
            resetRule.setCriticalPercentage(current.getCriticalPercentage());
            resetRule.setEffectiveFrom(now);
            resetRule.setIsActive(true);
            resetRule.setCreatedAt(now);
            limitRuleMapper.insert(resetRule);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void initDefaultLimits(Long cardId) {
        OffsetDateTime now = OffsetDateTime.now();
        // RECEIVE DAILY: 1000 DT
        createRuleIfAbsent(cardId, "RECEIVE", "DAILY", new BigDecimal("1000.000"), now);
        // RECEIVE MONTHLY: 5000 DT
        createRuleIfAbsent(cardId, "RECEIVE", "MONTHLY", new BigDecimal("5000.000"), now);
        // SEND DAILY: 1000 DT
        createRuleIfAbsent(cardId, "SEND", "DAILY", new BigDecimal("1000.000"), now);
        // SEND MONTHLY: 5000 DT
        createRuleIfAbsent(cardId, "SEND", "MONTHLY", new BigDecimal("5000.000"), now);
    }

    private void createRuleIfAbsent(Long cardId, String op, String period, BigDecimal amount, OffsetDateTime now) {
        if (limitRuleMapper.findActiveByCardIdAndOpAndPeriod(cardId, op, period).isEmpty()) {
            LimitRule rule = new LimitRule();
            rule.setCardId(cardId);
            rule.setOperationType(op);
            rule.setPeriodType(period);
            rule.setLimitAmount(amount);
            rule.setWarningPercentage(new BigDecimal("80.00"));
            rule.setCriticalPercentage(new BigDecimal("90.00"));
            rule.setEffectiveFrom(now);
            rule.setIsActive(true);
            rule.setCreatedAt(now);
            limitRuleMapper.insert(rule);
        }
    }

    private LimitUsageDto calculateUsage(Long cardId, LimitRule rule, OffsetDateTime referenceDate) {
        OffsetDateTime startDate = effectiveStartDate(rule, referenceDate);
        OffsetDateTime endDate = calculateEndDate(rule.getPeriodType(), referenceDate);

        BigDecimal current = ledgerTransactionMapper.sumAmountByCardAndDirectionBetween(
            cardId,
            "SEND".equalsIgnoreCase(rule.getOperationType()) ? "OUT" : "IN",
            startDate,
            endDate
        );

        if (current == null) {
            current = BigDecimal.ZERO;
        }

        BigDecimal limit = rule.getLimitAmount();
        BigDecimal remaining = limit.subtract(current);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal percentage = BigDecimal.ZERO;
        if (limit.compareTo(BigDecimal.ZERO) > 0) {
            percentage = current.multiply(new BigDecimal("100"))
                .divide(limit, 2, RoundingMode.HALF_UP);
        }

        String status = "NORMAL";
        if (current.compareTo(limit) >= 0) {
            status = "EXCEEDED";
        } else if (percentage.compareTo(rule.getCriticalPercentage()) >= 0) {
            status = "CRITICAL";
        } else if (percentage.compareTo(rule.getWarningPercentage()) >= 0) {
            status = "WARNING";
        }

        LimitUsageDto dto = new LimitUsageDto();
        dto.setId(rule.getId());
        dto.setOperationType(rule.getOperationType());
        dto.setPeriodType(rule.getPeriodType());
        dto.setCurrentAmount(current);
        dto.setLimitAmount(limit);
        dto.setRemainingAmount(remaining);
        dto.setPercentageUsed(percentage);
        dto.setStatus(status);
        dto.setDisplay(String.format("%s / %s TND (%s%%)", current.toPlainString(), limit.toPlainString(), percentage.toPlainString()));
        return dto;
    }

    private String resolveLimitOperation(String transactionType) {
        return switch (transactionType == null ? "" : transactionType.trim().toUpperCase()) {
            case "RECEIVE", "P2P_SELL" -> "RECEIVE";
            case "SEND", "P2P_BUY" -> "SEND";
            default -> transactionType;
        };
    }

    private OffsetDateTime calculateStartDate(String periodType, OffsetDateTime date) {
        if ("MONTHLY".equalsIgnoreCase(periodType)) {
            return date.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else {
            // DAILY
            return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
    }

    private OffsetDateTime calculateEndDate(String periodType, OffsetDateTime date) {
        if ("MONTHLY".equalsIgnoreCase(periodType)) {
            return date.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        } else {
            // DAILY
            return date.withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        }
    }

    private OffsetDateTime effectiveStartDate(LimitRule rule, OffsetDateTime date) {
        OffsetDateTime periodStart = calculateStartDate(rule.getPeriodType(), date);
        return rule.getEffectiveFrom() != null && rule.getEffectiveFrom().isAfter(periodStart)
            ? rule.getEffectiveFrom()
            : periodStart;
    }
}
