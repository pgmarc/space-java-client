package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BillingPeriodTest {

    private final LocalDateTime start = LocalDateTime.of(2025, 8, 15, 0, 0);
    private final LocalDateTime end = start.plusDays(30);

    @Test
    void givenZeroRenewalDaysShouldThrow() {

        BillingPeriod period = BillingPeriod.of(start, end);
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> period.setRenewalDays(Duration.ofHours(12)));
        assertEquals("your subscription cannot expire in less than one day", ex.getMessage());
    }

    @Test
    void givenStartDateAfterEndDateShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class,
                () -> BillingPeriod.of(start, start.minusDays(1)));
        assertEquals("startDate is after endDate", ex.getMessage());
    }

    @Test
    void givenRenewableDateShouldBeRenowable() {

        BillingPeriod billingPeriod = BillingPeriod.of(start,end);
        billingPeriod.setRenewalDays(Duration.ofDays(30));

        assertAll(
                () -> assertTrue(billingPeriod.isAutoRenewable()),
                () -> assertEquals(end.plusDays(30), billingPeriod.getRenewalDate().get()));
    }
}
