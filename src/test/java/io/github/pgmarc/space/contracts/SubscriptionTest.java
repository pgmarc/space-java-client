package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    private static final UserContact TEST_USER_CONTACT = UserContact.builder("123456789", "alexdoe")
            .build();
    private static final Service TEST_SERVICE = Service.builder("test", "alfa").plan("Foo").build();

    private BillingPeriod billingPeriod;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2025, 8, 15, 0, 0);
        LocalDateTime end = start.plusDays(30);
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

    @Test
    void givenSubscriptionAsJsonShouldCreateSubscription() {

        String startUtcString = "2025-04-18T00:00:00Z";
        String endUtcString = "2025-12-31T00:00:00Z";
        int renewalDays = 365;
        String zoomName = "zoom";
        String zoomVersion = "2025";
        String zoomPlan = "ENTERPRISE";
        String zoomExtraSeats = "extraSeats";
        int zoomExtraSeatsQuantity = 2;
        String zoomHugeMeetings = "hugeMeetings";
        int zoomHugeMeetingQuantity = 1;

        String petclinicService = "petclinic";
        String petclinicVersion = "2024";
        String petclinicPlan = "GOLD";
        String petclinicPetsAdoptionCentre = "petsAdoptionCentre";
        int petclinicPetsAdoptionCentreQuantity = 1;

        Map<String, Object> billingPeriodMap = Map.of(
                "startDate", startUtcString,
                "endDate", endUtcString,
                "autoRenew", true,
                "renewalDays", renewalDays);
        Map<String, Map<String, Object>> usageLevel = Map.of(
                zoomName,
                Map.of("maxSeats",
                        Map.of("consumed", 10.0)),
                petclinicService,
                Map.of("maxPets",
                        Map.of("consumed", 2.0),
                        "maxVisits", Map.of("consumed", 5.0, "resetTimestamp", "2025-07-31T00:00:00Z")));
        Map<String, String> contractedServices = Map.of(zoomName, zoomVersion, petclinicService, petclinicVersion);
        Map<String, String> contractedPlans = Map.of(zoomName, zoomPlan, petclinicService, petclinicPlan);
        Map<String, Map<String, Integer>> contractedAddOns = Map.of(
                zoomName,
                Map.of(zoomExtraSeats, zoomExtraSeatsQuantity,
                        zoomHugeMeetings, zoomHugeMeetingQuantity),
                petclinicService,
                Map.of(petclinicPetsAdoptionCentre, petclinicPetsAdoptionCentreQuantity));

        JSONObject snapshot1 = new JSONObject()
                .put("startDate", "2024-04-18T00:00:00Z")
                .put("endDate", "2024-05-18T00:00:00Z")
                .put("contractedServices", contractedServices)
                .put("subscriptionPlans", contractedPlans)
                .put("subscriptionAddOns", contractedAddOns);
        JSONArray history = new JSONArray()
                .put(snapshot1);

        JSONObject jsonInput = new JSONObject()
                .put("userContact", TEST_USER_CONTACT.toJson())
                .put("billingPeriod", billingPeriodMap)
                .put("usageLevel", usageLevel)
                .put("contractedServices", contractedServices)
                .put("subscriptionPlans", contractedPlans)
                .put("subscriptionAddOns", contractedAddOns)
                .put("history", history);

        Subscription actual = Subscription.fromJson(jsonInput);
        assertAll(
                () -> assertEquals(1, actual.getHistory().size()),
                () -> assertEquals(2, actual.getUsageLevels().size()),
                () -> assertEquals(2, actual.getServicesMap().size()));

    }
}
