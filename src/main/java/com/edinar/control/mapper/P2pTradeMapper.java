package com.edinar.control.mapper;

import com.edinar.control.domain.P2pTrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface P2pTradeMapper {

    int insert(P2pTrade trade);

    Optional<P2pTrade> findById(@Param("id") Long id);

    List<P2pTrade> findByUserId(@Param("userId") UUID userId);

    int updateTotals(
        @Param("id") Long id,
        @Param("totalBuyUsdt") BigDecimal totalBuyUsdt,
        @Param("totalSellUsdt") BigDecimal totalSellUsdt,
        @Param("totalBuyTnd") BigDecimal totalBuyTnd,
        @Param("totalSellTnd") BigDecimal totalSellTnd,
        @Param("totalBinanceFeeUsdt") BigDecimal totalBinanceFeeUsdt,
        @Param("totalBinanceFeeTnd") BigDecimal totalBinanceFeeTnd,
        @Param("grossProfitTnd") BigDecimal grossProfitTnd,
        @Param("netProfitTnd") BigDecimal netProfitTnd,
        @Param("updatedAt") OffsetDateTime updatedAt
    );

    int updateStatus(
        @Param("id") Long id,
        @Param("status") String status,
        @Param("closedAt") OffsetDateTime closedAt,
        @Param("updatedAt") OffsetDateTime updatedAt
    );
}
