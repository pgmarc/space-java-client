package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Subscription {

    private final UserContact userContact;
    private final Map<String, Service> services;
    private final BillingPeriod billingPeriod;
    private final List<Snapshot> history;
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

    public static Builder builder(UserContact userContact, BillingPeriod billingPeriod,
            Collection<Service> services) {
        return new Builder(userContact, billingPeriod).subscribeAll(services);
    }

    public LocalDateTime getStartDate() {
        return billingPeriod.getStartDate();
    }

    public LocalDateTime getEndDate() {
        return billingPeriod.getEndDate();
    }

    public Optional<Duration> getRenewalDuration() {
        return Optional.of(billingPeriod.getDuration());
    }

    public boolean isAutoRenewable() {
        return billingPeriod.isAutoRenewable();
    }

    /**
     * Checks whether a <code>date</code> is within the subscription interval.
     * A subscription is active if <code>date</code> is between subscription
     * start date and end date (both boundaries are inclusive).
     */
    public boolean isActive(LocalDateTime date) {
        Objects.requireNonNull(date, "date must not be null");
        return billingPeriod.isActive(ZonedDateTime.of(date, ZoneId.of("UTC")));
    }

    /**
     * A subscription has expired if given <code>date</code> is after
     * subscription interval.
     */
    public boolean isExpired(LocalDateTime date) {
        Objects.requireNonNull(date, "date must not be null");
        return billingPeriod.isExpired(ZonedDateTime.of(date, ZoneId.of("UTC")));
    }

    public Optional<LocalDateTime> getRenewalDate() {
        return billingPeriod.getRenewalDate();
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

    public List<Snapshot> getHistory() {
        return history;
    }

    public Map<String, Map<String, UsageLevel>> getUsageLevels() {
        return usageLevels;
    }

    public enum Keys {
        USER_CONTACT("userContact"),
        BILLING_PERIOD("billingPeriod"),
        CONTRACTED_SERVICES("contractedServices"),
        SUBSCRIPTION_PLANS("subscriptionPlans"),
        SUBSCRIPTION_ADDONS("subscriptionAddOns"),
        USAGE_LEVELS("usageLevels"),
        HISTORY("history");

        private final String name;

        Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final class Builder {

        private final BillingPeriod billingPeriod;
        private final UserContact userContact;
        private final Map<String, Service> services = new HashMap<>();
        private final List<Snapshot> history = new ArrayList<>();
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
            this.services.put(service.getName(), Objects.requireNonNull(service, "service must not be null"));
            return this;
        }

        private Map<String, Service> collectionToServiceMap(Collection<Service> services) {
            return services.stream()
                    .collect(Collectors.toUnmodifiableMap(Service::getName, service -> service));
        }

        public Builder subscribeAll(Collection<Service> services) {
            Objects.requireNonNull(services, "services must not be null");
            this.services.putAll(collectionToServiceMap(services));
            return this;
        }

        public Builder addSnapshots(Collection<Snapshot> snapshots) {
            Objects.requireNonNull(snapshots, "snapshots must not be null");
            this.history.addAll(snapshots);
            return this;
        }

        public Builder addUsageLevels(Map<String, Map<String, UsageLevel>> usageLevels) {
            this.usageLevels.putAll(usageLevels);
            return this;
        }

        public Subscription build() {
            Objects.requireNonNull(billingPeriod, "billingPeriod must not be null");
            Objects.requireNonNull(userContact, "userContact must not be null");
            return new Subscription(this);
        }
    }

    public static final class Snapshot {

        private final ZonedDateTime starDateTime;
        private final ZonedDateTime endDateTime;
        private final Map<String, Service> services;

        private Snapshot(ZonedDateTime startDateTime, ZonedDateTime endDateTime,
                Map<String, Service> services) {
            this.starDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.services = new HashMap<>(services);
        }

        public LocalDateTime getStartDate() {
            return starDateTime.toLocalDateTime();
        }

        public LocalDateTime getEndDate() {
            return endDateTime.toLocalDateTime();
        }

        public Map<String, Service> getServices() {
            return Collections.unmodifiableMap(services);
        }

        public Optional<Service> getService(String name) {
            return Optional.ofNullable(services.get(name));
        }

        public static Snapshot of(ZonedDateTime startDateTime, ZonedDateTime endDateTime,
                                  Map<String, Service> services) {
            return new Snapshot(startDateTime, endDateTime, services);
        }
    }
}
