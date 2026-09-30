package com.edinar.control.mapper;

import com.edinar.control.domain.LedgerTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface LedgerTransactionMapper {

    int insert(LedgerTransaction transaction);

    Optional<LedgerTransaction> findById(@Param("id") Long id);

    Optional<LedgerTransaction> findByIdForUpdate(@Param("id") Long id);

    List<LedgerTransaction> findByCardId(@Param("cardId") Long cardId);

    /**
     * Transactions paginées avec filtres optionnels.
     * Paramètres null ignorés (pas de filtrage sur ce champ).
     */
    List<LedgerTransaction> findByCardIdPaginated(
        @Param("cardId") Long cardId,
        @Param("type") String type,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate,
        @Param("isVoid") Boolean isVoid,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    List<LedgerTransaction> findByCardIdBetween(
        @Param("cardId") Long cardId,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate
    );

    /** Compte total pour la pagination. */
    long countByCardIdFiltered(
        @Param("cardId") Long cardId,
        @Param("type") String type,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate,
        @Param("isVoid") Boolean isVoid
    );

    /** Dernières N transactions d'une carte (non annulées). */
    List<LedgerTransaction> findRecentByCardId(
        @Param("cardId") Long cardId,
        @Param("limit") int limit
    );

    /** Somme des montants par type sur une période (exclut les transactions annulées). */
    BigDecimal sumAmountByCardAndTypeBetween(
        @Param("cardId") Long cardId,
        @Param("type") String type,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    /** Somme des montants par direction sur une période (exclut les annulées). */
    BigDecimal sumAmountByCardAndDirectionBetween(
        @Param("cardId") Long cardId,
        @Param("direction") String direction,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    BigDecimal sumAmountAndFeesByCardAndDirectionBetween(
        @Param("cardId") Long cardId,
        @Param("direction") String direction,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    /** Somme des montants par types de dépenses personnelles (OUT, non annulées). */
    BigDecimal sumPersonalExpensesByCardBetween(
        @Param("cardId") Long cardId,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Marque une transaction comme annulée (VOID).
     * Ne supprime jamais physiquement la transaction.
     */
    int voidTransaction(
        @Param("id") Long id,
        @Param("voidReason") String voidReason,
        @Param("voidedAt") OffsetDateTime voidedAt,
        @Param("voidedBy") UUID voidedBy
    );

    int deleteByCardIdBetween(
        @Param("cardId") Long cardId,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate
    );
}
