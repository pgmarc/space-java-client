package io.github.pgmarc.space.contracts;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SubscriptionSnapshot {

    private final LocalDateTime starDateTime;
    private final LocalDateTime enDateTime;
    private final Map<String, Service> services;

    private SubscriptionSnapshot(Subscription subscription) {
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

    static SubscriptionSnapshot of(Subscription subscription) {
        Objects.requireNonNull(subscription, "subscription must not be null");
        return new SubscriptionSnapshot(subscription);
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
        SubscriptionSnapshot other = (SubscriptionSnapshot) obj;
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
}
