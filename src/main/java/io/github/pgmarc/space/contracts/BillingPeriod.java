package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public final class BillingPeriod {

    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;
    private Duration renewalDays;

    private BillingPeriod(ZonedDateTime startDate, ZonedDateTime endDate, Duration renewalDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.renewalDays = renewalDays;
    }

    LocalDateTime getStartDate() {
        return startDate.toLocalDateTime();
    }

    LocalDateTime getEndDate() {
        return endDate.toLocalDateTime();
    }

    Duration getDuration() {
        return renewalDays;
    }

    boolean isExpired(LocalDateTime dateTime) {
        return endDate.isAfter(startDate);
    }

    boolean isAutoRenewable() {
        return renewalDays != null;
    }

    Optional<LocalDateTime> getRenewalDate() {
        return Optional.ofNullable(isAutoRenewable() ? endDate.plus(renewalDays).toLocalDateTime() : null);
    }

    public void setRenewalDays(Duration renewalDays) {
        validateRenewalDays(renewalDays);
        this.renewalDays = renewalDays;
    }

    private static void validateRenewalDays(Duration renewalDays) {
        if (renewalDays != null && renewalDays.toDays() <= 0) {
            throw new IllegalArgumentException("your subscription cannot expire in less than one day");
        }
    }

    public static BillingPeriod of(ZonedDateTime startDate, ZonedDateTime endDate) {
        return of(startDate, endDate, null);
    }

    public static BillingPeriod of(ZonedDateTime startDate, ZonedDateTime endDate, Duration renewalDays) {
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalStateException("startDate is after endDate");
        }
        validateRenewalDays(renewalDays);
        return new BillingPeriod(startDate, endDate, renewalDays);
    }

    public enum Keys {

        START_DATE("startDate"),
        END_DATE("endDate"),
        AUTORENEW("autoRenew"),
        RENEWAL_DAYS("renewalDays");

        private final String name;

        private Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((renewalDays == null) ? 0 : renewalDays.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BillingPeriod other = (BillingPeriod) obj;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (renewalDays == null) {
            if (other.renewalDays != null)
                return false;
        } else if (!renewalDays.equals(other.renewalDays))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "From " + startDate + " to " + endDate + ", renews in " + renewalDays;
    }
}
