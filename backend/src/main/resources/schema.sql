
CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payer VARCHAR(150) NOT NULL,
    provider VARCHAR(150) NOT NULL,
    cpt_code VARCHAR(20) NOT NULL,
    billed_amount DECIMAL(12, 2) NOT NULL,
    denial_reason VARCHAR(255),
    denied_at DATE,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT chk_paid_denial_reason
        CHECK (
            (status = 'paid' AND denial_reason IS NULL)
            OR
            (status <> 'paid' AND denial_reason IS NOT NULL)
        )
);

-- Index to support filtering claims by payer, status and denial date

