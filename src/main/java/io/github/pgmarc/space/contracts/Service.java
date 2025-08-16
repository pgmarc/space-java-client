package io.github.pgmarc.space.contracts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Service {

    private final String name;
    private final String version;
    private final Map<String, AddOn> addOns;
    private String plan;

    private Service(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.addOns = Collections.unmodifiableMap(builder.addOns);
        this.plan = builder.plan;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Optional<String> getPlan() {
        return Optional.ofNullable(plan);
    }

    public Optional<AddOn> getAddOn(String addOn) {
        Objects.requireNonNull(addOn, "key must not be null");
        return Optional.ofNullable(this.addOns.get(addOn));
    }

    public Set<AddOn> getAddOns() {
        return Set.copyOf(this.addOns.values());
    }

    public static Builder builder(String name, String version) {
        return new Builder(name, version);
    }

    public static final class Builder {

        private final String name;
        private final String version;
        private final Map<String, AddOn> addOns = new HashMap<>();
        private String plan;

        private Builder(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public Builder plan(String plan) {
            Objects.requireNonNull(plan, "plan must not be null");
            if (plan.isBlank()) {
                throw new IllegalArgumentException("plan must not be blank");
            }
            this.plan = plan;
            return this;
        }

        public Builder addOn(String name, long quantity) {
            Objects.requireNonNull(name, "add-on name must not be null");
            if (name.isBlank()) {
                throw new IllegalArgumentException("add-on name must no be blank");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException(name + " quantity must be greater than 0");
            }
            this.addOns.put(name, new AddOn(name, quantity));
            return this;
        }

        public Service build() {
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(version, "version must not be null");
            if (plan == null && this.addOns.isEmpty()) {
                throw new IllegalStateException("At least you have to be subscribed to a plan or add-on");
            }
            return new Service(this);
        }
    }
}
