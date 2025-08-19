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

    private static void validateUsageLevel(String name, double consumed) {
        Objects.requireNonNull(name, "usage limit name must not be null");
        if (consumed <= 0) {
            throw new IllegalArgumentException("consumption must be greater than 0");
        }
    }

    public static UsageLevel nonRenewable(String name, double consumed) {
        validateUsageLevel(name, consumed);
        return new UsageLevel(name, consumed);
    }

    public static UsageLevel renewable(String name, double consumed, LocalDateTime resetTimestamp) {
        validateUsageLevel(name, consumed);
        UsageLevel level = new UsageLevel(name, consumed);
        level.setResetTimestamp(resetTimestamp);
        return level;
    }
}
