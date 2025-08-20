package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;

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

    private enum Keys {

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

    static BillingPeriod fromJson(JSONObject json) {
        Objects.requireNonNull(json, "billing period json must not be null");
        OffsetDateTime startUtc = OffsetDateTime.parse(json.getString(Keys.START_DATE.toString()));
        OffsetDateTime endUtc = OffsetDateTime.parse(json.getString(Keys.END_DATE.toString()));
        BillingPeriod billingPeriod = BillingPeriod.of(startUtc.toLocalDateTime(), endUtc.toLocalDateTime());
        billingPeriod.setRenewalDays(Duration.ofDays(json.optLong(Keys.RENEWAL_DAYS.toString())));

        return billingPeriod;
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
