package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    private static final UserContact TEST_USER_CONTACT = UserContact.builder("123456789", "alexdoe")
            .build();
    private static final Service TEST_SERVICE = Service.builder("test", "alfa").plan("Foo").build();

    private BillingPeriod billingPeriod;

    @BeforeEach
    void setUp() {
        ZonedDateTime start = ZonedDateTime.parse("2025-08-15T00:00:00Z");
        ZonedDateTime end = start.plusDays(30);
        billingPeriod = BillingPeriod.of(start, end);
        billingPeriod.setRenewalDays(Duration.ofDays(30));
    }

    @Test
    void givenMultipleServicesInSubscriptionShouldCreate() {

        long renewalDays = 30;
        String service1Name = "Petclinic";
        String service2Name = "Petclinic Labs";

        Service service1 = Service.builder(service1Name, "v1").plan("GOLD").build();
        Service service2 = Service.builder(service2Name, "v2").plan("PLATINUM").build();

        Subscription sub = Subscription
                .builder(TEST_USER_CONTACT, billingPeriod, service1)
                .subscribe(service2)
                .renewIn(Duration.ofDays(renewalDays))
                .build();

        assertAll(() -> assertEquals(2, sub.getServices().size()),
                () -> assertEquals(service1, sub.getService(service1Name).get()),
                () -> assertEquals(service2, sub.getService(service2Name).get()));
    }

    @Test
    void whenNoRequiredParametersInputShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(null, billingPeriod, TEST_SERVICE)
                        .build());
        assertEquals("userContact must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(TEST_USER_CONTACT, null, TEST_SERVICE)
                        .build());
        assertEquals("billingPeriod must not be null", ex.getMessage());
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertDoesNotThrow(() -> Subscription.builder(TEST_USER_CONTACT, billingPeriod, TEST_SERVICE)
                .renewIn(null)
                .build());
    }

}
