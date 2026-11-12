package com.denialanalytics.backend.dto;

import java.math.BigDecimal;

public class PayerSummaryDto {

    private String payer;
    private Long deniedClaimCount;
    private BigDecimal totalDeniedAmount;

    public PayerSummaryDto(
            String payer,
            Long deniedClaimCount,
            BigDecimal totalDeniedAmount) {
        this.payer = payer;
        this.deniedClaimCount = deniedClaimCount;
        this.totalDeniedAmount = totalDeniedAmount;
    }

    public String getPayer() {
        return payer;
    }

    public Long getDeniedClaimCount() {
        return deniedClaimCount;
    }

    public BigDecimal getTotalDeniedAmount() {
        return totalDeniedAmount;
    }
}
