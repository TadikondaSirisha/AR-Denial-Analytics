package com.denialanalytics.backend.repository;

import com.denialanalytics.backend.dto.PayerSummaryDto;
import com.denialanalytics.backend.dto.ReasonSummaryDto;
import com.denialanalytics.backend.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends
        JpaRepository<Claim, Long>,
        JpaSpecificationExecutor<Claim> {

    @Query("""
        SELECT new com.denialanalytics.backend.dto.PayerSummaryDto(
            c.payer,
            COUNT(c),
            SUM(c.billedAmount)
        )
        FROM Claim c
        WHERE LOWER(c.status) = 'denied'
        GROUP BY c.payer
        ORDER BY SUM(c.billedAmount) DESC
    """)
    List<PayerSummaryDto> getDeniedAmountByPayer();

    @Query("""
        SELECT new com.denialanalytics.backend.dto.ReasonSummaryDto(
            c.denialReason,
            SUM(c.billedAmount)
        )
        FROM Claim c
        WHERE LOWER(c.status) = 'denied'
        GROUP BY c.denialReason
        ORDER BY SUM(c.billedAmount) DESC
    """)
    List<ReasonSummaryDto> getDeniedAmountByReason();

}
