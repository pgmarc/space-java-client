package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Subscription {

    private final UserContact userContact;
    private final Map<String, Service> services;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private Duration renewalDays;

    private Subscription(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.userContact = builder.userContact;
        this.services = Collections.unmodifiableMap(builder.services);
        this.renewalDays = builder.renewalDays;
    }

    public static Builder builder(UserContact userContact, LocalDateTime startDate, LocalDateTime endDate) {
        return new Builder(userContact, startDate, endDate);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getUserId() {
        return userContact.getUserId();
    }

    public String getUsername() {
        return userContact.getUsername();
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

    public Optional<Service> getService(String serviceName) {
        return Optional.ofNullable(this.services.get(serviceName));
    }

    public Set<Service> getServices() {
        return Set.copyOf(services.values());
    }

    public static final class Builder {

        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final UserContact userContact;
        private final Map<String, Service> services = new HashMap<>();
        private Duration renewalDays;

        private Builder(UserContact userContact, LocalDateTime startDate, LocalDateTime endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.userContact = userContact;
        }

        public Builder renewIn(Duration renewalDays) {
            if (renewalDays != null && renewalDays.toDays() <= 0) {
                throw new IllegalArgumentException("your subscription cannot expire in less than one day");
            }
            this.renewalDays = renewalDays;
            return this;
        }

        public Builder subscribe(Service service) {
            this.services.put(service.getName(), Objects.requireNonNull(service, "service must not be null"));
            return this;
        }

        public Subscription build() {
            Objects.requireNonNull(startDate, "startDate must not be null");
            Objects.requireNonNull(endDate, "endDate must not be null");
            Objects.requireNonNull(userContact, "userContact must not be null");
            if (startDate.isAfter(endDate)) {
                throw new IllegalStateException("startDate is after endDate");
            }
            if (services.isEmpty()) {
                throw new IllegalStateException("You have to be subscribed at least to a plan or an add-on");
            }
            return new Subscription(this);
        }

    }

}
