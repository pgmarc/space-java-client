package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscriptionRequestTest {

    private static final UserContact TEST_USER_CONTACT = UserContact.builder("123456789", "alexdoe")
            .build();

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

        SubscriptionRequest sub = SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .subscribe(service1)
                .subscribe(service2)
                .renewIn(Duration.ofDays(renewalDays))
                .build();

        assertEquals(Set.of(service1, service2), sub.getServices());
    }

    @Test
    void givenConsecutiveServiceCreationShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class, () -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .startService("test", "v1")
                .startService("incorrect", "v1")
                .build());
        assertEquals("you must build a service before creating another", ex.getMessage());
    }

    @Test
    void givenPlanCallBeforeCallingCreationServiceShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class, () -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .plan("foo")
                .build());
        assertEquals("you must call 'newService' before setting a plan: foo", ex.getMessage());
    }

    @Test
    void givenAddOnCallBeforeCallingCreationServiceShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class, () -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .addOn("foo", 1)
                .build());
        assertEquals("you must call 'newService' before setting an add-on: foo", ex.getMessage());
    }

    @Test
    void givenServiceBuildCallBeforeCreationServiceShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class, () -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .endService()
                .build());
        assertEquals("you must call 'newService' before adding a service", ex.getMessage());
    }

    @Test
    void whenNoRequiredParametersInputShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> SubscriptionRequest.builder(null)
                        .build());
        assertEquals("userContact must not be null", ex.getMessage());
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertDoesNotThrow(() -> SubscriptionRequest.builder(TEST_USER_CONTACT)
                .renewIn(null)
                .build());
    }

}
