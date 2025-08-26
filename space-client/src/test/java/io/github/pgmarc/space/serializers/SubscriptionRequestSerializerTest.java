package io.github.pgmarc.space.serializers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Set;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.github.pgmarc.space.contracts.Subscription;
import io.github.pgmarc.space.contracts.SubscriptionRequest;
import io.github.pgmarc.space.contracts.UserContact;

class SubscriptionRequestSerializerTest {

    @Test
    void givenSubscriptionRequestShouldSerialize() {

        UserContact userContact = UserContact.builder("01c36d29-0d6a-4b41-83e9-8c6d9310c508", "johndoe")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@my-domain.com")
                .phone("+34 666 666 666")
                .build();

        String zoom = "zoom";
        String petclinic = "petclinic";
        SubscriptionRequest subReq = SubscriptionRequest.builder(userContact)
                .renewIn(Duration.ofDays(365))
                .startService(zoom, "2025")
                .plan("ENTERPRISE")
                .addOn("extraSeats", 2)
                .addOn("hugeMeetings", 1)
                .endService()
                .startService("petclinic", "2024")
                .plan("GOLD")
                .addOn("petsAdoptionCentre", 1)
                .endService()
                .build();

        SubscriptionRequestSerializer serializer = new SubscriptionRequestSerializer();
        JSONObject actual = serializer.toJson(subReq);

        Set<String> serviceKeys = Set.of(zoom, petclinic);
        Set<String> addOnZoomKeys = Set.of("hugeMeetings", "extraSeats");
        Set<String> addOnPetclinicKeys = Set.of( "petsAdoptionCentre");

        Set<String> actualZoomAddOnKeys =  actual.getJSONObject(Subscription.Keys.SUBSCRIPTION_ADDONS.toString()).getJSONObject(zoom).keySet();
        Set<String> actualPetclinicAddOnKeys =  actual.getJSONObject(Subscription.Keys.SUBSCRIPTION_ADDONS.toString()).getJSONObject(petclinic).keySet();

        assertAll(
                () -> assertEquals(serviceKeys,
                        actual.getJSONObject(Subscription.Keys.CONTRACTED_SERVICES.toString()).keySet()),
                () -> assertEquals(serviceKeys, actual.getJSONObject(Subscription.Keys.SUBSCRIPTION_PLANS.toString()).keySet()),
                () -> assertEquals(serviceKeys, actual.getJSONObject(Subscription.Keys.SUBSCRIPTION_ADDONS.toString()).keySet()),
                () -> assertEquals(addOnZoomKeys, actualZoomAddOnKeys),
                () -> assertEquals(addOnPetclinicKeys, actualPetclinicAddOnKeys));

    }

}
