package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UsageLevelTest {

    @Test
    void givenNonRenewableUsageLimitShouldCreate() {

        String usageLimitName = "maxPets";
        double consumption = 5;
        UsageLevel usageLevel = UsageLevel.nonRenewable(usageLimitName, consumption);

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
                () -> assertThrows(NullPointerException.class, () -> UsageLevel.nonRenewable(null, consumption)),
                () -> assertThrows(IllegalArgumentException.class, () -> UsageLevel.nonRenewable(usageLimitName, -1)));
    }

    @Test
    void givenRenewableUsageLimitShouldCreate() {

        String usageLimitName = "maxTokens";
        double consumption = 300;
        LocalDateTime resetTimestamp = LocalDateTime.of(2025, 8, 19, 0, 0);
        UsageLevel usageLevel = UsageLevel.renewable(usageLimitName, consumption, resetTimestamp);

        assertAll(
                () -> assertEquals(usageLimitName, usageLevel.getName()),
                () -> assertEquals(consumption, usageLevel.getConsumption()),
                () -> assertEquals(resetTimestamp, usageLevel.getResetTimestamp().get()),
                () -> assertTrue(usageLevel.isRenewableUsageLimit()));
    }

}
