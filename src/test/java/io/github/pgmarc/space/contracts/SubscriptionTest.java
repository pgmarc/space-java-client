package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class SubscriptionTest {

    private static final UserContact userContact = UserContact.builder("123456789", "alexdoe")
            .build();
    private static final Service service = Service.builder("test", "alfa").plan("Foo").build();

    private static final LocalDateTime START = LocalDateTime.of(2025, 8, 15, 0, 0);

    private static final LocalDateTime END = START.plusDays(30);

    @Test
    void givenMultipleServicesInSubscriptionShouldCreate() {

        long renewalDays = 30;
        LocalDateTime renewalDate = END.plusDays(renewalDays);
        String service1Name = "Petclinic";
        String service2Name = "Petclinic Labs";

        Service service1 = Service.builder(service1Name, "v1").plan("GOLD").build();
        Service service2 = Service.builder(service2Name, "v2").plan("PLATINUM").build();

        Subscription sub = Subscription
                .builder(userContact, START, END, service1)
                .subscribe(service2)
                .renewIn(Duration.ofDays(renewalDays))
                .build();

        assertAll(
                () -> assertEquals(START, sub.getStartDate()),
                () -> assertEquals(END, sub.getEndDate()),
                () -> assertTrue(sub.isAutoRenewable()),
                () -> assertEquals(renewalDate, sub.getRenewalDate().get()));

        assertEquals(2, sub.getServices().size());
        assertEquals(service1, sub.getService(service1Name).get());
        assertEquals(service2, sub.getService(service2Name).get());
    }

    @Test
    void whenNoRequiredParametersInputShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(null, START, END, service)
                        .build());
        assertEquals("userContact must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(userContact, null, END, service)
                        .build());
        assertEquals("startDate must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(userContact, START, null, service)
                        .build());
        assertEquals("endDate must not be null", ex.getMessage());
    }

    @Test
    void givenStartDateAfterEndDateShouldThrow() {

        LocalDateTime end = START.minusDays(1);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> Subscription.builder(userContact, START, end, service)
                        .build());
        assertEquals("startDate is after endDate", ex.getMessage());
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertDoesNotThrow(() -> Subscription.builder(userContact, START, END, service)
                .renewIn(null)
                .build());
    }

    @Test
    void givenZeroRenewalDaysShouldThrow() {

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> Subscription.builder(userContact, START, END, service)
                        .renewIn(Duration.ofDays(0))
                        .build());
        assertEquals("your subscription cannot expire in less than one day", ex.getMessage());
    }

    private Subscription firstPetclinicSub(Service petclinic) {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 0, 0);

        return Subscription.builder(userContact, start, end, petclinic)
                .build();
    }

    private Subscription secondSubscription(Service petclinic, Service petclinicLabs) {

        LocalDateTime start = LocalDateTime.of(2024, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 0, 0);

        return Subscription
                .builder(userContact, start, end, petclinic)
                .subscribe(petclinicLabs)
                .build();
    }

    @Test
    void givenASubscriptionHistoryShouldBeVisbile() {

        String petclinic = "Petclinic";
        String petclinicLabs = "Petclinic Labs";
        Service petclinicV1 = Service.builder(petclinic, "v1").plan("FREE").build();
        Service petclinicV2 = Service.builder(petclinic, "v2").plan("GOLD").build();
        Service petclinicLabsV1 = Service.builder(petclinicLabs, "v1").plan("PLATINUM").build();

        Subscription sub1 = firstPetclinicSub(petclinicV1);
        Subscription sub2 = secondSubscription(petclinicV2, petclinicLabsV1);

        Subscription currentSubscription = Subscription
                .builder(userContact, START, END, petclinicV2)
                .addSnapshot(sub1)
                .addSnapshot(sub2)
                .build();

        List<SubscriptionSnapshot> history = new ArrayList<>();
        SubscriptionSnapshot snaphot1 = SubscriptionSnapshot.of(sub1);
        SubscriptionSnapshot snaphot2 = SubscriptionSnapshot.of(sub2);

        history.add(snaphot1);
        history.add(snaphot2);

        assertEquals(history, currentSubscription.getHistory());

        assertAll(
                () -> assertEquals(2, currentSubscription.getHistory().size()),
                () -> assertEquals(history, currentSubscription.getHistory()));

    }

}
