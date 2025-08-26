package io.github.pgmarc.space.contracts;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public final class UsageLevel {

    private final String name;
    private final double consumed;
    private ZonedDateTime resetTimestamp;

    public enum Keys {
        CONSUMED("consumed"),
        RESET_TIMESTAMP("resetTimestamp");

        private final String name;

        private Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private UsageLevel(String name, double consumed, ZonedDateTime resetTimestamp) {
        this.name = name;
        this.consumed = consumed;
        this.resetTimestamp = resetTimestamp;
    }

    public String getName() {
        return name;
    }

    public Optional<LocalDateTime> getResetTimestamp() {
        return resetTimestamp != null ? Optional.of(resetTimestamp.toLocalDateTime()) : Optional.empty();
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

    public static UsageLevel of(String name, double consumed) {
        return of(name, consumed, null);
    }

    public static UsageLevel of(String name, double consumed, ZonedDateTime resetTimestamp) {
        validateUsageLevel(name, consumed);
        return new UsageLevel(name, consumed, resetTimestamp);
    }
}
