package io.github.pgmarc.space.contracts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

import org.json.JSONArray;
import org.json.JSONObject;

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

    public static Builder builder(UserContact usagerContact, BillingPeriod billingPeriod,
            Collection<Service> services) {
        return new Builder(usagerContact, billingPeriod).subscribeAll(services);
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

    private enum Keys {
        USER_CONTACT("userContact"),
        BILLING_PERIOD("billingPeriod"),
        CONTRACTED_SERVICES("contractedServices"),
        SUBSCRIPTION_PLANS("subscriptionPlans"),
        SUBSCRIPTION_ADDONS("subscriptionAddOns"),
        USAGE_LEVEL("usageLevel"),
        HISTORY("history");

        private final String name;

        private Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static Map<String, Service> servicesFromJson(JSONObject json) {
        JSONObject contractedServices = json.getJSONObject(Keys.CONTRACTED_SERVICES.toString());
        JSONObject subscriptionPlans = json.getJSONObject(Keys.SUBSCRIPTION_PLANS.toString());
        JSONObject subscriptionAddOns = json.getJSONObject(Keys.SUBSCRIPTION_ADDONS.toString());
        Map<String, Service> services = new HashMap<>();

        for (String serviceName : contractedServices.keySet()) {
            Service.Builder serviceBuilder = Service.builder(serviceName, contractedServices.getString(serviceName))
                    .plan(subscriptionPlans.getString(serviceName));

            for (String addOnName : subscriptionAddOns.getJSONObject(serviceName).keySet()) {
                serviceBuilder.addOn(addOnName, subscriptionAddOns.getJSONObject(serviceName).getLong(addOnName));
            }
            services.put(serviceName, serviceBuilder.build());
        }
        return services;
    }

    private static Map<String, Map<String, UsageLevel>> usageLevelsFromJson(JSONObject usageLevel) {
        Objects.requireNonNull(usageLevel, "usage level must not be null");

        Map<String, Map<String, UsageLevel>> usageLevelMap = new HashMap<>();
        if (usageLevel.isEmpty()) {
            return Collections.unmodifiableMap(usageLevelMap);
        }
        for (String serviceName : usageLevel.keySet()) {
            Map<String, UsageLevel> serviceLevels = new HashMap<>();
            JSONObject rawServiceUsageLevels = usageLevel.getJSONObject(serviceName);
            for (String usageLimitName : rawServiceUsageLevels.keySet()) {
                JSONObject rawUsageLevel = rawServiceUsageLevels.getJSONObject(usageLimitName);
                LocalDateTime expirationDate = null;
                if (rawUsageLevel.has("resetTimestamp")) {
                    expirationDate = ZonedDateTime.parse(rawUsageLevel.getString("resetTimestamp")).toLocalDateTime();
                }
                UsageLevel ul = UsageLevel.of(serviceName, rawUsageLevel.getDouble("consumed"), expirationDate);
                serviceLevels.put(usageLimitName, ul);
            }
            usageLevelMap.put(serviceName, Collections.unmodifiableMap(serviceLevels));
        }
        return usageLevelMap;
    }

    static Subscription fromJson(JSONObject json) {

        BillingPeriod billingPeriod = BillingPeriod.fromJson(json.getJSONObject(Keys.BILLING_PERIOD.toString()));
        UserContact userContact = UserContact.fromJson(json.getJSONObject(Keys.USER_CONTACT.toString()));
        Map<String, Map<String, UsageLevel>> usageLevels = usageLevelsFromJson(
                json.getJSONObject(Keys.USAGE_LEVEL.toString()));
        Map<String, Service> services = servicesFromJson(json);
        List<Snapshot> history = Snapshot.fromJson(json.optJSONArray(Keys.HISTORY.toString()));
        return Subscription.builder(userContact, billingPeriod, services.values())
                .addUsageLevels(usageLevels).addSnapshots(history).build();
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

        private Builder addSnapshots(Collection<Snapshot> snaphsots) {
            Objects.requireNonNull(snaphsots, "snapshots must not be null");
            this.history.addAll(snaphsots);
            return this;
        }

        private Builder addUsageLevels(Map<String, Map<String, UsageLevel>> usageLevels) {
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

        private final LocalDateTime starDateTime;
        private final LocalDateTime enDateTime;
        private final Map<String, Service> services;

        private Snapshot(LocalDateTime startDateTime, LocalDateTime endDateTime,
                Map<String, Service> services) {
            this.starDateTime = startDateTime;
            this.enDateTime = endDateTime;
            this.services = Collections.unmodifiableMap(services);
        }

        private Snapshot(Subscription subscription) {
            this.starDateTime = subscription.getStartDate();
            this.enDateTime = subscription.getEndDate();
            this.services = subscription.getServicesMap();
        }

        public LocalDateTime getStartDate() {
            return starDateTime;
        }

        public LocalDateTime getEndDate() {
            return enDateTime;
        }

        public Map<String, Service> getServices() {
            return services;
        }

        public Optional<Service> getService(String name) {
            return Optional.ofNullable(services.get(name));
        }

        static Snapshot of(Subscription subscription) {
            Objects.requireNonNull(subscription, "subscription must not be null");
            return new Snapshot(subscription);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((starDateTime == null) ? 0 : starDateTime.hashCode());
            result = prime * result + ((enDateTime == null) ? 0 : enDateTime.hashCode());
            result = prime * result + ((services == null) ? 0 : services.hashCode());
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
            Snapshot other = (Snapshot) obj;
            if (starDateTime == null) {
                if (other.starDateTime != null)
                    return false;
            } else if (!starDateTime.equals(other.starDateTime))
                return false;
            if (enDateTime == null) {
                if (other.enDateTime != null)
                    return false;
            } else if (!enDateTime.equals(other.enDateTime))
                return false;
            if (services == null) {
                if (other.services != null)
                    return false;
            } else if (!services.equals(other.services))
                return false;
            return true;
        }

        private static List<Snapshot> fromJson(JSONArray rawHistory) {
            List<Snapshot> history = new ArrayList<>();
            for (int i = 0; i < rawHistory.length(); i++) {
                JSONObject snaphsot = rawHistory.getJSONObject(i);
                OffsetDateTime startUtc = OffsetDateTime.parse(snaphsot.getString("startDate"));
                OffsetDateTime end = OffsetDateTime.parse(snaphsot.getString("endDate"));
                history.add(new Snapshot(startUtc.toLocalDateTime(), end.toLocalDateTime(),
                        Subscription.servicesFromJson(snaphsot)));
            }
            return history;
        }

    }

}
