package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class BillingPeriod {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private Duration renewalDays;

    private BillingPeriod(LocalDateTime startDate, LocalDateTime enDateTime) {
        this.startDate = startDate;
        this.endDate = enDateTime;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isExpired(LocalDateTime dateTime) {
        return dateTime.isAfter(endDate);
    }

    public boolean isAutoRenewable() {
        return renewalDays != null;
    }

    public Optional<LocalDateTime> getRenewalDate() {
        return Optional.ofNullable(isAutoRenewable() ? endDate.plus(renewalDays) : null);
    }

    void setRenewalDays(Duration renewalDays) {
        if (renewalDays != null && renewalDays.toDays() <= 0) {
            throw new IllegalArgumentException("your subscription cannot expire in less than one day");
        }
        this.renewalDays = renewalDays;
    }

    static BillingPeriod of(LocalDateTime startDate, LocalDateTime endDate) {
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalStateException("startDate is after endDate");
        }
        return new BillingPeriod(startDate, endDate);
    }

}
