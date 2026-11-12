package com.denialanalytics.backend.dto;

import java.math.BigDecimal;

public class ReasonSummaryDto {

    private String denialReason;
    private BigDecimal totalDeniedAmount;

    public ReasonSummaryDto(
            String denialReason,
            BigDecimal totalDeniedAmount) {
        this.denialReason = denialReason;
        this.totalDeniedAmount = totalDeniedAmount;
    }

    public String getDenialReason() {
        return denialReason;
    }

    public BigDecimal getTotalDeniedAmount() {
        return totalDeniedAmount;
    }
}
