package io.github.pgmarc.space.contracts;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.ZonedDateTime;

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

        assertThat(sub.getServices()).contains(service1, service2);
    }

    @Test
    void givenConsecutiveServiceCreationShouldThrow() {

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .startService("test", "v1")
                .startService("incorrect", "v1")
                .build()).withMessage("you must build a service before creating another");
    }

    @Test
    void givenPlanCallBeforeCallingCreationServiceShouldThrow() {

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .plan("foo")
                .build()).withMessage("you must call 'newService' before setting a plan: foo");
    }

    @Test
    void givenAddOnCallBeforeCallingCreationServiceShouldThrow() {

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .addOn("foo", 1)
                .build()).withMessage("you must call 'newService' before setting an add-on: foo");

    }

    @Test
    void givenServiceBuildCallBeforeCreationServiceShouldThrow() {

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> SubscriptionRequest
                .builder(TEST_USER_CONTACT)
                .endService()
                .build()).withMessage("you must call 'newService' before adding a service");
    }

    @Test
    void whenNoRequiredParametersInputShouldThrow() {

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> SubscriptionRequest.builder(null)
                .build()).withMessage("userContact must not be null");
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertThat(SubscriptionRequest.builder(TEST_USER_CONTACT)
                .renewIn(null)
                .build().getRenewalDays()).isNull();

    }

}
