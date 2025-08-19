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

    private static final UserContact TEST_USER_CONTACT = UserContact.builder("123456789", "alexdoe")
            .build();
    private static final Service TEST_SERVICE = Service.builder("test", "alfa").plan("Foo").build();

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
                .builder(TEST_USER_CONTACT, START, END, service1)
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
                () -> Subscription.builder(null, START, END, TEST_SERVICE)
                        .build());
        assertEquals("userContact must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(TEST_USER_CONTACT, null, END, TEST_SERVICE)
                        .build());
        assertEquals("startDate must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class,
                () -> Subscription.builder(TEST_USER_CONTACT, START, null, TEST_SERVICE)
                        .build());
        assertEquals("endDate must not be null", ex.getMessage());
    }

    @Test
    void givenStartDateAfterEndDateShouldThrow() {

        LocalDateTime end = START.minusDays(1);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> Subscription.builder(TEST_USER_CONTACT, START, end, TEST_SERVICE)
                        .build());
        assertEquals("startDate is after endDate", ex.getMessage());
    }

    @Test
    void givenOptionalRenewalDaysShouldNotThrow() {

        assertDoesNotThrow(() -> Subscription.builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                .renewIn(null)
                .build());
    }

    @Test
    void givenZeroRenewalDaysShouldThrow() {

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> Subscription.builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                        .renewIn(Duration.ofDays(0))
                        .build());
        assertEquals("your subscription cannot expire in less than one day", ex.getMessage());
    }

    private Subscription firstPetclinicSub(Service petclinic) {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 0, 0);

        return Subscription.builder(TEST_USER_CONTACT, start, end, petclinic)
                .build();
    }

    private Subscription secondSubscription(Service petclinic, Service petclinicLabs) {

        LocalDateTime start = LocalDateTime.of(2024, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 0, 0);

        return Subscription
                .builder(TEST_USER_CONTACT, start, end, petclinic)
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
                .builder(TEST_USER_CONTACT, START, END, petclinicV2)
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

    @Test
    void givenSubscriptionWithUsageLevelsShouldCreate() {

        String usageLimitName = "maxAlfa";
        UsageLevel ul1 = UsageLevel.nonRenewable(usageLimitName, 5);

        Subscription sub = Subscription
                .builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                .addUsageLevel(TEST_SERVICE.getName(), ul1)
                .build();

        assertEquals(ul1, sub.getUsageLevels().get(TEST_SERVICE.getName()).get(usageLimitName));

    }

    @Test
    void givenSubscriptionToAServiceRelatedUsageLevelShoudBeEmpty() {

        Subscription sub = Subscription
                .builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                .build();

        assertAll(() -> assertTrue(sub.getUsageLevels().containsKey(TEST_SERVICE.getName())),
                () -> assertTrue(sub.getUsageLevels().get(TEST_SERVICE.getName()).isEmpty()));
    }

    @Test
    void givenDuplicateUsageLevelShouldNotRegisterTwice() {
        String usageLimitName = "maxAlfa";
        UsageLevel ul1 = UsageLevel.nonRenewable(usageLimitName, 5);

        Subscription sub = Subscription
                .builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                .addUsageLevel(TEST_SERVICE.getName(), ul1)
                .addUsageLevel(TEST_SERVICE.getName(), ul1)
                .build();

        assertEquals(1, sub.getUsageLevels().get(TEST_SERVICE.getName()).size());
    }

    @Test
    void givenNonExistentServiceShouldThrowWhenAddingUsageLevel() {

        UsageLevel ul1 = UsageLevel.nonRenewable("maxAlfa", 5);

        Exception ex = assertThrows(IllegalStateException.class, () -> Subscription
                .builder(TEST_USER_CONTACT, START, END, TEST_SERVICE)
                .addUsageLevel("nonExistent", ul1)
                .build());

        assertEquals("Service 'nonExistent' doesn't exist. Register it previously", ex.getMessage());

    }

}
