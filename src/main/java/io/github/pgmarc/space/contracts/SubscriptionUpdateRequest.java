package io.github.pgmarc.space.contracts;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SubscriptionUpdateRequest {

    private final Set<Service> services = new HashSet<>();
    private Service.Builder serviceBuilder;

    private SubscriptionUpdateRequest() {

    }

    public Set<Service> getServices() {
        return Set.copyOf(services);
    }

    public static SubscriptionUpdateRequest builder() {
        return new SubscriptionUpdateRequest();
    }

    public SubscriptionUpdateRequest service(String name, String version) {
        this.serviceBuilder = Service.builder(name, version);
        return this;
    }

    public SubscriptionUpdateRequest plan(String plan) {
        Objects.requireNonNull(serviceBuilder, "you call service first");
        serviceBuilder.plan(plan);
        return this;
    }

    public SubscriptionUpdateRequest addOn(String name, long quantity) {
        Objects.requireNonNull(serviceBuilder, "you call service first");
        this.serviceBuilder.addOn(name, quantity);
        return this;
    }

    public SubscriptionUpdateRequest add() {
        Objects.requireNonNull(serviceBuilder, "you call service first");
        this.services.add(serviceBuilder.build());
        this.serviceBuilder = null;
        return this;
    }

}
