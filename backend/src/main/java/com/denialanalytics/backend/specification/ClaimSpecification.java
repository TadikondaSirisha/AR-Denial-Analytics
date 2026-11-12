package com.denialanalytics.backend.specification;

import com.denialanalytics.backend.entity.Claim;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClaimSpecification {

    public static Specification<Claim> filterClaims(
            String payer,
            String reason,
            LocalDate startDate,
            LocalDate endDate) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                cb.equal(
                    cb.lower(root.get("status")),
                    "denied"
                )
            );

            if (payer != null && !payer.isBlank()) {
                predicates.add(
                    cb.equal(
                        cb.lower(root.get("payer")),
                        payer.trim().toLowerCase()
                    )
                );
            }

            if (reason != null && !reason.isBlank()) {
                predicates.add(
                    cb.equal(
                        cb.lower(root.get("denialReason")),
                        reason.trim().toLowerCase()
                    )
                );
            }

            if (startDate != null) {
                predicates.add(
                    cb.greaterThanOrEqualTo(
                        root.get("deniedAt"), startDate
                    )
                );
            }

            if (endDate != null) {
                predicates.add(
                    cb.lessThanOrEqualTo(
                        root.get("deniedAt"), endDate
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
