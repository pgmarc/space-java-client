package io.github.pgmarc.space.serializers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.github.pgmarc.space.contracts.Subscription;
import io.github.pgmarc.space.contracts.UserContact;

class SubscriptionSerializerTest {

    private final SubscriptionDeserializer serializer = new SubscriptionDeserializer();

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

        UserContact userContact = UserContact.builder("123456789", "alex").build();

        JSONObject jsonInput = new JSONObject()
                .put("userContact", userContact.toJson())
                .put("billingPeriod", billingPeriodMap)
                .put("usageLevel", usageLevel)
                .put("contractedServices", contractedServices)
                .put("subscriptionPlans", contractedPlans)
                .put("subscriptionAddOns", contractedAddOns)
                .put("history", history);

        Subscription actual = serializer.fromJson(jsonInput);
        assertAll(
                () -> assertEquals(1, actual.getHistory().size()),
                () -> assertEquals(2, actual.getUsageLevels().size()),
                () -> assertEquals(2, actual.getServicesMap().size()));

    }

}
