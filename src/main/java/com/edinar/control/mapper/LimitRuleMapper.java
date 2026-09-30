package com.edinar.control.mapper;

import com.edinar.control.domain.LimitRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LimitRuleMapper {

    int insert(LimitRule rule);

    List<LimitRule> findActiveByCardId(@Param("cardId") Long cardId);

    Optional<LimitRule> findActiveByCardIdAndOpAndPeriod(
        @Param("cardId") Long cardId,
        @Param("operationType") String operationType,
        @Param("periodType") String periodType
    );

    int deactivate(@Param("id") Long id, @Param("effectiveTo") java.time.OffsetDateTime effectiveTo);
}
