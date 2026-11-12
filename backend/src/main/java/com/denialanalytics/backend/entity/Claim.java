package com.denialanalytics.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String payer;

    @Column(nullable = false, length = 150)
    private String provider;

    @Column(name = "cpt_code", nullable = false, length = 20)
    private String cptCode;

    @Column(name = "billed_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal billedAmount;

    @Column(name = "denial_reason")
    private String denialReason;

    @Column(name = "denied_at")
    private LocalDate deniedAt;

    @Column(nullable = false, length = 30)
    private String status;

    public Claim() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCptCode() {
        return cptCode;
    }

    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getDenialReason() {
        return denialReason;
    }

    public void setDenialReason(String denialReason) {
        this.denialReason = denialReason;
    }

    public LocalDate getDeniedAt() {
        return deniedAt;
    }

    public void setDeniedAt(LocalDate deniedAt) {
        this.deniedAt = deniedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
