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
    private final BillingPeriod billingPeriod;
    private final List<SubscriptionSnapshot> history;
    private final Map<String, Map<String, UsageLevel>> usageLevels;

    private Subscription(Builder builder) {
        this.userContact = builder.userContact;
        this.billingPeriod = builder.billingPeriod;
        this.services = Collections.unmodifiableMap(builder.services);
        this.history = Collections.unmodifiableList(builder.history);
        this.usageLevels = Collections.unmodifiableMap(builder.usageLevels);
    }

    public static Builder builder(UserContact userContact, BillingPeriod billingPeriod,
            Service service) {
        return new Builder(userContact, billingPeriod).subscribe(service);
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public LocalDateTime getStartDate() {
        return billingPeriod.getStartDate();
    }

    public LocalDateTime getEndDate() {
        return billingPeriod.getEndDate();
    }

    public boolean isAutoRenewable() {
        return billingPeriod.isAutoRenewable();
    }

    public String getUserId() {
        return userContact.getUserId();
    }

    public String getUsername() {
        return userContact.getUsername();
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

        private final BillingPeriod billingPeriod;
        private final UserContact userContact;
        private final Map<String, Service> services = new HashMap<>();
        private final List<SubscriptionSnapshot> history = new ArrayList<>();
        private final Map<String, Map<String, UsageLevel>> usageLevels = new HashMap<>();

        private Builder(UserContact userContact, BillingPeriod billingPeriod) {
            this.billingPeriod = billingPeriod;
            this.userContact = userContact;
        }

        public Builder renewIn(Duration renewalDays) {
            this.billingPeriod.setRenewalDays(renewalDays);
            return this;
        }

        public Builder subscribe(Service service) {
            if (!hasService(service.getName())) {
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
            if (!hasService(serviceName)) {
                throw new IllegalStateException("Service '" + serviceName + "' doesn't exist. Register it previously");
            }

            boolean hasUsageLevel = usageLevels.get(serviceName).containsKey(usageLevel.getName());

            if (!hasUsageLevel) {
                this.usageLevels.get(serviceName).put(usageLevel.getName(), usageLevel);
            }

            return this;
        }

        private boolean hasService(String name) {
            return services.containsKey(name);
        }

        public Subscription build() {
            Objects.requireNonNull(billingPeriod, "billingPeriod must not be null");
            Objects.requireNonNull(userContact, "userContact must not be null");
            return new Subscription(this);
        }

    }

}
