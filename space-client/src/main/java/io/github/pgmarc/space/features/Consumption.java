package io.github.pgmarc.space.features;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Consumption {

    private final Set<Item<?>> items;

    private Consumption(Builder builder) {
        this.items = Collections.unmodifiableSet(builder.items);
    }

    public Set<Item<?>> getConsumption() {
        return items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Item<T> {
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

    public static class Builder {

        private final Set<Item<?>> items = new HashSet<>();

        private void validateFeatureId(String service, String usageLimit) {
            Objects.requireNonNull(service, "service name must not be null");
            Objects.requireNonNull(usageLimit, "usage limit name must not be null");
        }

        public Builder addInt(String serviceName, String usageLimit, int quantity) {
            validateFeatureId(serviceName, usageLimit);
            this.items.add(new Item<>(serviceName, usageLimit, quantity));
            return this;
        }

        public Builder addLong(String serviceName, String usageLimit, long quantity) {
            validateFeatureId(serviceName, usageLimit);
            this.items.add(new Item<>(serviceName, usageLimit, quantity));
            return this;
        }

        public Builder addFloat(String serviceName, String usageLimit, float quantity) {
            validateFeatureId(serviceName, usageLimit);
            this.items.add(new Item<>(serviceName, usageLimit, quantity));
            return this;
        }

        public Builder addDouble(String serviceName, String usageLimit, double quantity) {
            validateFeatureId(serviceName, usageLimit);
            this.items.add(new Item<>(serviceName, usageLimit, quantity));
            return this;
        }

        public Consumption build() {
            if (items.isEmpty()) {
                throw new IllegalStateException("consumption must not be empty");
            }
            return new Consumption(this);
        }

    }
}
