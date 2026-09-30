package com.edinar.control.mapper;

import com.edinar.control.domain.P2pOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface P2pOrderMapper {

    int insert(P2pOrder order);

    Optional<P2pOrder> findById(@Param("id") Long id);

    List<P2pOrder> findByTradeId(@Param("tradeId") Long tradeId);
}
