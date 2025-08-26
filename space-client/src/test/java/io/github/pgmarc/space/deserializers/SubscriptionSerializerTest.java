package io.github.pgmarc.space.deserializers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.github.pgmarc.space.contracts.Subscription;

class SubscriptionSerializerTest {

    private final SubscriptionDeserializer serializer = new SubscriptionDeserializer();

    @Test
    void givenSubscriptionAsJsonShouldCreateSubscription() {

        JSONObject input = new JSONObject(Map.of(
                "id", "68050bd09890322c57842f6f",
                "userContact", Map.of(
                        "userId", "01c36d29-0d6a-4b41-83e9-8c6d9310c508",
                        "username", "johndoe",
                        "fistName", "John",
                        "lastName", "Doe",
                        "email", "john.doe@my-domain.com",
                        "phone", "+34 666 666 666"),
                "billingPeriod", Map.of(
                        "startDate", "2025-12-31T00:00:00Z",
                        "endDate", "2025-12-31T00:00:00Z",
                        "autoRenew", true,
                        "renewalDays", 365),
                "usageLevel", Map.of(
                        "zoom", Map.of(
                                "maxSeats", Map.of("consumed", 10)),
                        "petclinic", Map.of(
                                "maxPets", Map.of("consumed", 2),
                                "maxVisits", Map.of(
                                        "consumed", 5,
                                        "resetTimeStamp", "2025-07-31T00:00:00Z"))),
                "contractedServices", Map.of(
                        "zoom", "2025",
                        "petclinic", "2024"),
                "subscriptionPlans", Map.of(
                        "zoom", "ENTERPRISE",
                        "petclinic", "GOLD"),
                "subscriptionAddOns", Map.of(
                        "zoom", Map.of(
                                "extraSeats", 2,
                                "hugeMeetings", 1),
                        "petclinic", Map.of(
                                "petsAdoptionCentre", 1)),
                "history", List.of(
                        Map.of(
                                "startDate", "2025-12-31T00:00:00Z",
                                "endDate", "2025-12-31T00:00:00Z",
                                "contractedServices", Map.of(
                                        "zoom", "2025",
                                        "petclinic", "2024"),
                                "subscriptionPlans", Map.of(
                                        "zoom", "ENTERPRISE",
                                        "petclinic", "GOLD"),
                                "subscriptionAddOns", Map.of(
                                        "zoom", Map.of(
                                                "extraSeats", 2,
                                                "hugeMeetings", 1),
                                        "petclinic", Map.of(
                                                "petsAdoptionCentre", 1))))));

        Subscription actual = serializer.fromJson(input);
        assertAll(
                () -> assertEquals(1, actual.getHistory().size()),
                () -> assertEquals(2, actual.getUsageLevels().size()),
                () -> assertEquals(2, actual.getServicesMap().size()));

    }

}
