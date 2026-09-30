package com.edinar.control.service;

import com.edinar.control.domain.FeeRule;
import com.edinar.control.dto.p2p.CreateFeeRuleRequest;
import com.edinar.control.dto.p2p.FeeRuleResponse;
import com.edinar.control.mapper.FeeRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeeRuleService {

    private final FeeRuleMapper feeRuleMapper;

    public FeeRuleService(FeeRuleMapper feeRuleMapper) {
        this.feeRuleMapper = feeRuleMapper;
    }

    @Transactional(readOnly = true)
    public List<FeeRuleResponse> getAllRules() {
        return feeRuleMapper.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Crée une nouvelle règle de frais.
     * IMPORTANT : on ne modifie jamais une règle existante — on en crée une nouvelle.
     * L'ancienne règle reste en base pour la traçabilité historique.
     */
    @Transactional(rollbackFor = Exception.class)
    public FeeRuleResponse createRule(CreateFeeRuleRequest request) {
        OffsetDateTime now = OffsetDateTime.now();

        FeeRule rule = new FeeRule();
        rule.setPlatform(request.getPlatform());
        rule.setMarket(request.getMarket());
        rule.setAsset(request.getAsset());
        rule.setSide(request.getSide() != null ? request.getSide().toUpperCase() : null);
        rule.setRole(request.getRole() != null ? request.getRole().toUpperCase() : null);
        rule.setFeeType(request.getFeeType().toUpperCase());
        rule.setFeeValue(request.getFeeValue());
        rule.setFeeCurrency(request.getFeeCurrency().toUpperCase());
        rule.setMinFee(request.getMinFee());
        rule.setMaxFee(request.getMaxFee());
        rule.setEffectiveFrom(now);
        rule.setEffectiveTo(null);
        rule.setIsActive(true);
        rule.setNotes(request.getNotes());
        rule.setCreatedAt(now);

        feeRuleMapper.insert(rule);
        return toResponse(rule);
    }

    /**
     * Recherche la règle Binance active pour un couple (side, role) à la date donnée.
     * Retourne Optional.empty() si aucune règle trouvée (les frais seront 0).
     */
    @Transactional(readOnly = true)
    public Optional<FeeRule> findActiveRule(
        String platform, String market, String asset,
        String side, String role,
        OffsetDateTime atDate
    ) {
        return feeRuleMapper.findActiveRule(platform, market, asset, side, role, atDate);
    }

    /**
     * Calcule les frais Binance en USDT pour une quantité donnée.
     *
     * Formule PERCENTAGE : fee_usdt = quantityUsdt × (feeValue / 100)
     * Formule FIXED       : fee_usdt = feeValue (fixe, indépendant de la quantité)
     *
     * Applique ensuite min_fee / max_fee si définis.
     */
    public BigDecimal calculateFeeUsdt(FeeRule rule, BigDecimal quantityUsdt) {
        BigDecimal feeUsdt;

        if ("PERCENTAGE".equalsIgnoreCase(rule.getFeeType())) {
            feeUsdt = quantityUsdt
                .multiply(rule.getFeeValue())
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
        } else {
            // FIXED
            feeUsdt = rule.getFeeValue();
        }

        // Appliquer min
        if (rule.getMinFee() != null && feeUsdt.compareTo(rule.getMinFee()) < 0) {
            feeUsdt = rule.getMinFee();
        }
        // Appliquer max
        if (rule.getMaxFee() != null && feeUsdt.compareTo(rule.getMaxFee()) > 0) {
            feeUsdt = rule.getMaxFee();
        }

        return feeUsdt.setScale(8, RoundingMode.HALF_UP);
    }

    /**
     * Convertit un montant en USDT en TND via le taux de conversion fourni.
     */
    public BigDecimal convertUsdtToTnd(BigDecimal amountUsdt, BigDecimal conversionRate) {
        return amountUsdt
            .multiply(conversionRate)
            .setScale(3, RoundingMode.HALF_UP);
    }

    private FeeRuleResponse toResponse(FeeRule rule) {
        FeeRuleResponse r = new FeeRuleResponse();
        r.setId(rule.getId());
        r.setPlatform(rule.getPlatform());
        r.setMarket(rule.getMarket());
        r.setAsset(rule.getAsset());
        r.setSide(rule.getSide());
        r.setRole(rule.getRole());
        r.setFeeType(rule.getFeeType());
        r.setFeeValue(rule.getFeeValue());
        r.setFeeCurrency(rule.getFeeCurrency());
        r.setMinFee(rule.getMinFee());
        r.setMaxFee(rule.getMaxFee());
        r.setEffectiveFrom(rule.getEffectiveFrom());
        r.setEffectiveTo(rule.getEffectiveTo());
        r.setIsActive(rule.getIsActive());
        r.setNotes(rule.getNotes());
        r.setCreatedAt(rule.getCreatedAt());
        return r;
    }
}
