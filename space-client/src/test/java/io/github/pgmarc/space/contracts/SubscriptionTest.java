package io.github.pgmarc.space.contracts;

import static org.assertj.core.api.Assertions.*;


import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

public class SubscriptionTest {

    private static final UserContact TEST_CONTACT = UserContact.builder("123456789", "alex").build();
    private static final Service TEST_SERVICE = Service.builder("petclinic", "2025").plan("GOLD").build();

    @Test
    void givenSubscriptionShould() {

        String userId = "123456789";
        String username = "alex";
        UserContact contact = UserContact.builder(userId, username).build();
        ZonedDateTime start = ZonedDateTime.parse("2025-08-28T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2025-08-29T00:00:00Z");

        String serviceName = "petclinic";
        BillingPeriod billingPeriod = BillingPeriod.of(start, end, Duration.ofDays(45));
        Service service = Service.builder(serviceName, "2025").plan("GOLD").build();

        ZonedDateTime snapshotStart = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        ZonedDateTime snapshotEnd = ZonedDateTime.parse("2025-01-01T00:00:00Z");
        Service snapshotService = Service.builder(serviceName, "2024").plan("FREE").build();
        Subscription.Snapshot snapshot1 = Subscription.Snapshot.of(snapshotStart.toLocalDateTime(),
            snapshotEnd.toLocalDateTime(), Map.of("petclinic", snapshotService));
        Set<Subscription.Snapshot> history = Set.of(snapshot1);

        Subscription subscription = Subscription.builder(contact, billingPeriod, service)
            .addSnapshots(history).build();

        assertThat(subscription.getUsername()).isEqualTo(username);
        assertThat(subscription.getStartDate()).isEqualTo(start.toLocalDateTime());
        assertThat(subscription.getEndDate()).isEqualTo(end.toLocalDateTime());
        assertThat(subscription.isAutoRenewable()).isTrue();
        assertThat(subscription.getRenewalDate()).hasValue(end.plusDays(45).toLocalDateTime());
        assertThat(subscription.getService(serviceName)).hasValue(service);
        assertThat(subscription.getHistory()).contains(snapshot1);
        assertThat(subscription.getHistory().get(0).getStartDate()).isEqualTo(snapshotStart.toLocalDateTime());
        assertThat(subscription.getHistory().get(0).getEndDate()).isEqualTo(snapshotEnd.toLocalDateTime());
        assertThat(subscription.getHistory().get(0).getServices()).isNotEmpty();
        assertThat(subscription.getHistory().get(0).getService(serviceName)).hasValue(snapshotService);
    }

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
