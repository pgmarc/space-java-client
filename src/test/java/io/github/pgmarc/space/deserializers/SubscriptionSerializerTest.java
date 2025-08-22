package io.github.pgmarc.space.deserializers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.github.pgmarc.space.contracts.Subscription;

class SubscriptionSerializerTest {

    private final SubscriptionDeserializer serializer = new SubscriptionDeserializer();

    @Test
    void givenSubscriptionAsJsonShouldCreateSubscription() {

        String content = null;
        try {
            byte[] raw = Files.readAllBytes(Paths.get("src", "test", "resources", "subscription-response.json"));
            content = new String(raw);
        } catch (IOException e) {
            fail(e.getCause());
        }

        Subscription actual = serializer.fromJson(new JSONObject(content));
        assertAll(
                () -> assertEquals(1, actual.getHistory().size()),
                () -> assertEquals(2, actual.getUsageLevels().size()),
                () -> assertEquals(2, actual.getServicesMap().size()));

    }

}
