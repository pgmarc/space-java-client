package io.github.pgmarc.space.features;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class UsageLimitConsumption {

    private final Set<Item<? extends  Number>> items;

    private UsageLimitConsumption(Builder builder) {
        this.items = Collections.unmodifiableSet(builder.items);
    }

    public Set<Item<? extends  Number>> getConsumption() {
        return items;
    }

    public static Builder builder(String service) {
        Objects.requireNonNull(service, "service name must not be null");
        return new Builder(service);
    }

    public static final class Builder {

        private final String service;
        private final Set<Item<? extends  Number>> items = new HashSet<>();

        private Builder(String service) {
            this.service = service.toLowerCase();
        }

        private void validateFeatureId(String usageLimit) {
            Objects.requireNonNull(usageLimit, "usage limit name must not be null");
        }

        public Builder addInt(String usageLimit, int quantity) {
            validateFeatureId(usageLimit);
            this.items.add(new Item<>(service, usageLimit, quantity));
            return this;
        }

        public Builder addLong(String usageLimit, long quantity) {
            validateFeatureId(usageLimit);
            this.items.add(new Item<>(service, usageLimit, quantity));
            return this;
        }

        public Builder addFloat(String usageLimit, float quantity) {
            validateFeatureId(usageLimit);
            this.items.add(new Item<>(service, usageLimit, quantity));
            return this;
        }

        public Builder addDouble(String usageLimit, double quantity) {
            validateFeatureId(usageLimit);
            this.items.add(new Item<>(service, usageLimit, quantity));
            return this;
        }

        public UsageLimitConsumption build() {
            if (service.isBlank()) {
                throw new IllegalStateException("service name must not be blank");
            }
            if (items.isEmpty()) {
                throw new IllegalStateException("usage limits consumption must not be empty");
            }
            return new UsageLimitConsumption(this);
        }
    }

    public static final class Item<T> {
        private final String serviceName;
        private final String usageLimit;
        private final T quantity;

        private Item(String serviceName, String usageLimit, T quantity) {
            this.serviceName = serviceName;
            this.usageLimit = usageLimit;
            this.quantity = quantity;
        }

        public String getServiceName() {
            return  serviceName;
        }

        public String getUsageLimit() {
            return  usageLimit;
        }

        public T getQuantity() {
            return quantity;
        }
    }
}
