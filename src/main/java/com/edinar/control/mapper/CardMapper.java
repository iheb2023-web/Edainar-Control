package com.edinar.control.mapper;

import com.edinar.control.domain.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface CardMapper {

    int insert(Card card);

    Optional<Card> findById(@Param("id") Long id);

    /**
     * SELECT FOR UPDATE — verrouille la ligne pour les opérations financières.
     * À utiliser dans tous les services qui modifient le solde de la carte.
     */
    Optional<Card> findByIdForUpdate(@Param("id") Long id);

    /**
     * Ownership enforcement — retourne la carte uniquement si elle appartient au userId.
     */
    Optional<Card> findByIdAndUserId(@Param("id") Long id, @Param("userId") UUID userId);

    /**
     * SELECT FOR UPDATE avec vérification d'ownership.
     * Utilisé dans LedgerService pour les opérations financières sécurisées.
     */
    Optional<Card> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") UUID userId);

    List<Card> findAll();

    List<Card> findByUserId(@Param("userId") UUID userId);

    int update(Card card);

    int delete(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedAt") OffsetDateTime updatedAt);

    int updateBalance(@Param("id") Long id, @Param("currentBalance") BigDecimal currentBalance, @Param("updatedAt") OffsetDateTime updatedAt);

    int updateVerifiedBalance(
        @Param("id") Long id,
        @Param("lastVerifiedBalance") BigDecimal lastVerifiedBalance,
        @Param("lastVerifiedAt") OffsetDateTime lastVerifiedAt,
        @Param("currentBalance") BigDecimal currentBalance,
        @Param("updatedAt") OffsetDateTime updatedAt
    );
}
