package io.github.pgmarc.space.contracts;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class UsageLevel {

    private final String name;
    private final double consumed;
    private LocalDateTime resetTimestamp;

    private UsageLevel(String name, double consumed) {
        this.name = name;
        this.consumed = consumed;
    }

    private UsageLevel(String name, double consumed, LocalDateTime resetTimestamp) {
        this.name = name;
        this.consumed = consumed;
        this.resetTimestamp = resetTimestamp;
    }

    public String getName() {
        return name;
    }

    public Optional<LocalDateTime> getResetTimestamp() {
        return Optional.ofNullable(resetTimestamp);
    }

    public boolean isRenewableUsageLimit() {
        return resetTimestamp != null;
    }

    public double getConsumption() {
        return consumed;
    }

    private static void validateUsageLevel(String name, double consumed) {
        Objects.requireNonNull(name, "usage limit name must not be null");
        if (consumed <= 0) {
            throw new IllegalArgumentException("consumption must be greater than 0");
        }
    }

    static UsageLevel of(String name, double consumed) {
        validateUsageLevel(name, consumed);
        return new UsageLevel(name, consumed);
    }

    static UsageLevel of(String name, double consumed, LocalDateTime resetTimestamp) {
        validateUsageLevel(name, consumed);
        UsageLevel level = new UsageLevel(name, consumed, resetTimestamp);
        return level;
    }
}
