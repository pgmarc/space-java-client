package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.json.JSONObject;
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

        BillingPeriod billingPeriod = BillingPeriod.of(start, end);
        billingPeriod.setRenewalDays(Duration.ofDays(30));

        assertAll(
                () -> assertTrue(billingPeriod.isAutoRenewable()),
                () -> assertEquals(end.plusDays(30), billingPeriod.getRenewalDate().get()));
    }

    @Test
    void givenJsonShouldCreateBillingPeriod() {

        String startUtc = "2024-08-20T12:00Z";
        String endUtc = "2025-08-20T12:00Z";
        LocalDateTime start = OffsetDateTime.parse(startUtc).toLocalDateTime();
        LocalDateTime end = OffsetDateTime.parse(endUtc).toLocalDateTime();
        BillingPeriod expected = BillingPeriod.of(start, end);
        expected.setRenewalDays(Duration.ofDays(30));

        JSONObject input = new JSONObject().put("startDate", startUtc)
        .put("endDate", endUtc).put("autoRenew", true).put("renewalDays", 30L);

        assertEquals(expected, BillingPeriod.fromJson(input));
    }

    @Test
    void giveNullJsonShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class, () -> BillingPeriod.fromJson(null));
        assertEquals("billing period json must not be null", ex.getMessage());
    }
}
