package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Subscription {

    private final UserContact userContact;
    private final Map<String, Service> services;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Duration renewalDays;
    private final List<SubscriptionSnapshot> history;
    private final Map<String, Map<String, UsageLevel>> usageLevels;

    private Subscription(Builder builder) {
        this.userContact = builder.userContact;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.renewalDays = builder.renewalDays;
        this.services = Collections.unmodifiableMap(builder.services);
        this.history = Collections.unmodifiableList(builder.history);
        this.usageLevels = Collections.unmodifiableMap(builder.usageLevels);
    }

    public static Builder builder(UserContact userContact, LocalDateTime startDate, LocalDateTime endDate,
            Service service) {
        return new Builder(userContact, startDate, endDate).subscribe(service);
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

    public Map<String, Service> getServicesMap() {
        return services;
    }

    public Set<Service> getServices() {
        return Set.copyOf(services.values());
    }

    public List<SubscriptionSnapshot> getHistory() {
        return history;
    }

    public Map<String, Map<String, UsageLevel>> getUsageLevels() {
        return usageLevels;
    }

    public static final class Builder {

        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final UserContact userContact;
        private final Map<String, Service> services = new HashMap<>();
        private final List<SubscriptionSnapshot> history = new ArrayList<>();
        private final Map<String, Map<String, UsageLevel>> usageLevels = new HashMap<>();
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
            if (!services.containsKey(service.getName())) {
                this.usageLevels.put(service.getName(), new HashMap<>());
            }
            this.services.put(service.getName(), Objects.requireNonNull(service, "service must not be null"));
            return this;
        }

        Builder addSnapshot(Subscription subscription) {
            Objects.requireNonNull(subscription, "subscription must not be null");
            this.history.add(SubscriptionSnapshot.of(subscription));
            return this;
        }

        Builder addUsageLevel(String serviceName, UsageLevel usageLevel) {
            if (!services.containsKey(serviceName)) {
                throw new IllegalStateException("Service '" + serviceName + "' doesn't exist. Register it previously");
            }

            if (!usageLevels.get(serviceName).containsKey(usageLevel.getName())) {
                this.usageLevels.get(serviceName).put(usageLevel.getName(), usageLevel);
            }

            return this;
        }

        public Subscription build() {
            Objects.requireNonNull(startDate, "startDate must not be null");
            Objects.requireNonNull(endDate, "endDate must not be null");
            Objects.requireNonNull(userContact, "userContact must not be null");
            if (startDate.isAfter(endDate)) {
                throw new IllegalStateException("startDate is after endDate");
            }
            return new Subscription(this);
        }

    }

}
