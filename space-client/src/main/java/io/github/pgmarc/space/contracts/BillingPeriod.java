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

    boolean isActive(ZonedDateTime date) {
        return  (startDate.isEqual(date) || startDate.isBefore(date)) &&
                (endDate.isAfter(date) || endDate.isEqual(date));
    }

    boolean isExpired(ZonedDateTime date) {
        return startDate.isBefore(date) && endDate.isBefore(date);
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

        Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BillingPeriod that = (BillingPeriod) o;
        return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(renewalDays, that.renewalDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, renewalDays);
    }

    @Override
    public String toString() {
        return "From " + startDate + " to " + endDate + ", renews in " + renewalDays;
    }
}
