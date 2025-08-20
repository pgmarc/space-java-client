package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class UsageLevelTest {

    @Test
    void givenNonRenewableUsageLimitShouldCreate() {

        String usageLimitName = "maxPets";
        double consumption = 5;
        UsageLevel usageLevel = UsageLevel.of(usageLimitName, consumption);

        assertAll(
                () -> assertEquals(usageLimitName, usageLevel.getName()),
                () -> assertEquals(consumption, usageLevel.getConsumption()),
                () -> assertFalse(usageLevel.isRenewableUsageLimit()));
    }

    @Test
    void givenInvalidParamertersShouldThrow() {

        String usageLimitName = "maxPets";
        double consumption = 5;

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> UsageLevel.of(null, consumption)),
                () -> assertThrows(IllegalArgumentException.class, () -> UsageLevel.of(usageLimitName, -1)));
    }

    @Test
    void givenRenewableUsageLimitShouldCreate() {

        String usageLimitName = "maxTokens";
        double consumption = 300;
        ZonedDateTime resetTimestamp = ZonedDateTime.parse("2025-08-19T00:00:00Z");
        UsageLevel usageLevel = UsageLevel.of(usageLimitName, consumption, resetTimestamp);

        assertAll(
                () -> assertEquals(usageLimitName, usageLevel.getName()),
                () -> assertEquals(consumption, usageLevel.getConsumption()),
                () -> assertEquals(resetTimestamp.toLocalDateTime(), usageLevel.getResetTimestamp().get()),
                () -> assertTrue(usageLevel.isRenewableUsageLimit()));
    }

}
