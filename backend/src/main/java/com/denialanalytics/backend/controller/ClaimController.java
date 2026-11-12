package com.denialanalytics.backend.controller;

import com.denialanalytics.backend.dto.PayerSummaryDto;
import com.denialanalytics.backend.dto.ReasonSummaryDto;
import com.denialanalytics.backend.entity.Claim;
import com.denialanalytics.backend.service.ClaimService;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/analytics/payers")
    public List<PayerSummaryDto> getPayerAnalytics() {
        return claimService.getDeniedAmountByPayer();
    }

    @GetMapping("/analytics/reasons")
    public List<ReasonSummaryDto> getReasonAnalytics() {
        return claimService.getDeniedAmountByReason();
    }

    @GetMapping("/claims")
    public Page<Claim> getDeniedClaims(
            @RequestParam(name = "payer", required = false)
            String payer,

            @RequestParam(name = "reason", required = false)
            String reason,

            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(name = "page", defaultValue = "0")
            int page,

            @RequestParam(name = "size", defaultValue = "10")
            int size) {

        return claimService.getDeniedClaims(
            payer, reason, startDate, endDate, page, size
        );
    }
}
