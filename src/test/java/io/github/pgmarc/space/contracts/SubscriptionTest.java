package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class SubscriptionTest {

    private static final UserContact userContact = UserContact.builder("123456789", "alexdoe")
            .build();
    private static final Service service = Service.builder("test", "alfa").plan("Foo").build();

    private static final LocalDateTime start = LocalDateTime.of(2025, 8, 15, 0, 0);

    private static final LocalDateTime end = start.plusDays(30);

    @Test
    void givenNoServiceInSubscriptionShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class,
                () -> Subscription.builder(userContact, start, end).build());

        assertEquals("You have to be subscribed at least to a plan or an add-on", ex.getMessage());
    }

    @Test
    void givenMultipleServicesInSubscriptionShouldCreate() {

        long renewalDays = 30;
        LocalDateTime renewalDate = end.plusDays(renewalDays);
        String service1Name = "Petclinic";
        String service2Name = "Petclinic Labs";

        Service service1 = Service.builder(service1Name, "v1").plan("GOLD").build();
        Service service2 = Service.builder(service2Name, "v2").plan("PLATINUM").build();

        Subscription sub = Subscription
                .builder(userContact, start, end)
                .subscribe(service1)
                .subscribe(service2)
                .renewIn(Duration.ofDays(renewalDays))
                .build();

        assertAll(
                () -> assertEquals(start, sub.getStartDate()),
                () -> assertEquals(end, sub.getEndDate()),
                () -> assertTrue(sub.isAutoRenewable()),
                () -> assertEquals(renewalDate, sub.getRenewalDate().get()));

        assertEquals(2, sub.getServices().size());
        assertEquals(service1, sub.getService(service1Name).get());
        assertEquals(service2, sub.getService(service2Name).get());
    }

    @Test
    void whenNoRequiredParametersInputShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class, () -> Subscription.builder(null, start, end)
                .subscribe(service)
                .build());
        assertEquals("userContact must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class, () -> Subscription.builder(userContact, null, end)
                .subscribe(service)
                .build());
        assertEquals("startDate must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class, () -> Subscription.builder(userContact, start, null)
                .subscribe(service)
                .build());
        assertEquals("endDate must not be null", ex.getMessage());
    }

    @Test
    void givenStartDateAfterEndDateShouldThrow() {

        LocalDateTime end = start.minusDays(1);

        Exception ex = assertThrows(IllegalStateException.class, () -> Subscription.builder(userContact, start, end)
                .subscribe(service)
                .build());
        assertEquals("startDate is after endDate", ex.getMessage());
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertDoesNotThrow(() -> Subscription.builder(userContact, start, end)
                .subscribe(service)
                .renewIn(null)
                .build());
    }

    @Test
    void givenZeroRenewalDaysShouldThrow() {

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Subscription.builder(userContact, start, end)
                .subscribe(service)
                .renewIn(Duration.ofDays(0))
                .build());
        assertEquals("your subscription cannot expire in less than one day", ex.getMessage());
    }

}
