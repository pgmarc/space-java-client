package io.github.pgmarc.space.contracts;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class UsageLevel {

    private final String serviceName;
    private final String name;
    private final double consumed;
    private LocalDateTime resetTimestamp;

    private UsageLevel(String serviceName, String name, double consumed) {
        this.serviceName = serviceName;
        this.name = name;
        this.consumed = consumed;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName() {
        return name;
    }

    public Optional<LocalDateTime> getResetTimestamp() {
        return Optional.ofNullable(resetTimestamp);
    }

    private void setResetTimestamp(LocalDateTime resetTimestamp) {
        this.resetTimestamp = resetTimestamp;
    }

    public boolean isRenewableUsageLimit() {
        return resetTimestamp != null;
    }

    public double getConsumption() {
        return consumed;
    }

    private static void validateUsageLevel(String serviceName, String name, double consumed) {
        Objects.requireNonNull(serviceName, "service name must not be null");
        Objects.requireNonNull(name, "usage limit name must not be null");
        if (consumed <= 0) {
            throw new IllegalArgumentException("consumption must be greater than 0");
        }
    }

    public static UsageLevel nonRenewable(String serviceName, String name, double consumed) {
        validateUsageLevel(serviceName, name, consumed);
        return new UsageLevel(serviceName, name, consumed);
    }

    public static UsageLevel renewable(String serviceName, String name, double consumed, LocalDateTime resetTimestamp) {
        validateUsageLevel(serviceName, name, consumed);
        UsageLevel level = new UsageLevel(serviceName, name, consumed);
        level.setResetTimestamp(resetTimestamp);
        return level;
    }
}
