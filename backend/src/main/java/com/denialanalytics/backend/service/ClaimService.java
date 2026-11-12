package com.denialanalytics.backend.service;

import com.denialanalytics.backend.dto.PayerSummaryDto;
import com.denialanalytics.backend.dto.ReasonSummaryDto;
import com.denialanalytics.backend.entity.Claim;
import com.denialanalytics.backend.repository.ClaimRepository;
import com.denialanalytics.backend.specification.ClaimSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<PayerSummaryDto> getDeniedAmountByPayer() {
        return claimRepository.getDeniedAmountByPayer();
    }

    public List<ReasonSummaryDto> getDeniedAmountByReason() {
        return claimRepository.getDeniedAmountByReason();
    }

    public Page<Claim> getDeniedClaims(
            String payer,
            String reason,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
            );
        }

        if (startDate != null && endDate != null
                && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                "End date cannot be earlier than start date"
            );
        }

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "deniedAt")
        );

        return claimRepository.findAll(
            ClaimSpecification.filterClaims(
                payer, reason, startDate, endDate
            ),
            pageable
        );
    }
}
