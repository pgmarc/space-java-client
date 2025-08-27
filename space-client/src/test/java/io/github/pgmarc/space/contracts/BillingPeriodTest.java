package io.github.pgmarc.space.contracts;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class BillingPeriodTest {

    private final ZonedDateTime start = ZonedDateTime.parse("2025-08-15T00:00:00Z");
    private final ZonedDateTime end = start.plusDays(30);

    @Test
    void givenZeroRenewalDaysShouldThrow() {

        BillingPeriod period = BillingPeriod.of(start, end);
        Duration duration = Duration.ofHours(12);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> period.setRenewalDays(duration))
                .withMessage("your subscription cannot expire in less than one day");
    }

    @Test
    void givenStartDateAfterEndDateShouldThrow() {

        ZonedDateTime endDate = start.minusDays(1);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> BillingPeriod.of(start, endDate))
                .withMessage("startDate is after endDate");
    }

    @Test
    void givenRenewableDateShouldBeRenowable() {

        int days = 30;
        BillingPeriod billingPeriod = BillingPeriod.of(start, end);
        billingPeriod.setRenewalDays(Duration.ofDays(days));

        assertThat(billingPeriod.getDuration().toDays()).isEqualTo(days);
        assertThat(billingPeriod.getRenewalDate()).isPresent().hasValue(end.plusDays(30).toLocalDateTime());
    }

    @Test
    void givenBillingPeriodCheckIfSubscriptionIsExpired() {

        ZonedDateTime startDate = ZonedDateTime.parse("2025-08-15T00:00:00Z");
        ZonedDateTime endDate = startDate.plusDays(30);

        BillingPeriod period = BillingPeriod.of(startDate, endDate);

        LocalDateTime dateToTest = LocalDateTime.of(2026, 1, 1, 0, 0);

        assertThat(period.isExpired(dateToTest)).isTrue();
    }
}
