package com.edinar.control.mapper;

import com.edinar.control.domain.FeeRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FeeRuleMapper {

    int insert(FeeRule feeRule);

    List<FeeRule> findAll();

    /**
     * Cherche la règle active la plus spécifique pour un couple (platform, market, asset, side, role).
     * Fallback : side=null (toutes directions), role=null (tous rôles).
     */
    Optional<FeeRule> findActiveRule(
        @Param("platform") String platform,
        @Param("market") String market,
        @Param("asset") String asset,
        @Param("side") String side,
        @Param("role") String role,
        @Param("atDate") OffsetDateTime atDate
    );
}
