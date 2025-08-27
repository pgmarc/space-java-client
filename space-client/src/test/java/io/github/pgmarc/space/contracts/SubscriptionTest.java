package io.github.pgmarc.space.contracts;

import static org.assertj.core.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class SubscriptionTest {

    private static final UserContact TEST_CONTACT = UserContact.builder("123456789", "alex").build();
    private static final Service TEST_SERVICE = Service.builder("petclinic", "2025").plan("GOLD").build();

    @Test
    void givenDateAfterSubscriptionShouldBeExpired() {

        ZonedDateTime start = ZonedDateTime.parse("2025-01-01T00:00:00Z");
        ZonedDateTime end =  ZonedDateTime.parse("2025-02-01T00:00:00Z");
        BillingPeriod period = BillingPeriod.of(start, end);
        Subscription subscription = Subscription.builder(TEST_CONTACT, period, TEST_SERVICE).build();

        LocalDateTime dateToCheck = ZonedDateTime.parse("2025-02-01T00:00:01Z").toLocalDateTime();
        assertThat(subscription.isExpired(dateToCheck)).isTrue();
        assertThat(subscription.isActive(dateToCheck)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings =
        {
            "2025-01-01T00:00:00Z",
            "2025-01-15T00:00:00Z",
            "2025-02-01T00:00:00Z"
        })
    void givenDateInsideSubscriptionShouldBeActive(String utcDate) {

        ZonedDateTime start = ZonedDateTime.parse("2025-01-01T00:00:00Z");
        ZonedDateTime end =  ZonedDateTime.parse("2025-02-01T00:00:00Z");
        BillingPeriod period = BillingPeriod.of(start, end);
        Subscription subscription = Subscription.builder(TEST_CONTACT, period, TEST_SERVICE).build();

        LocalDateTime dateToCheck = ZonedDateTime.parse(utcDate).toLocalDateTime();
        assertThat(subscription.isExpired(dateToCheck)).isFalse();
        assertThat(subscription.isActive(dateToCheck)).isTrue();
    }
}
