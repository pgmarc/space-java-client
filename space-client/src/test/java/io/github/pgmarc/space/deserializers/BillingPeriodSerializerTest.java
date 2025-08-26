package io.github.pgmarc.space.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.github.pgmarc.space.contracts.BillingPeriod;

class BillingPeriodSerializerTest {

    private final BillingPeriodDeserializer serializer = new BillingPeriodDeserializer();


    
    @Test
    void givenJsonShouldCreateBillingPeriod() {

        String startUtc = "2024-08-20T12:00Z";
        String endUtc = "2025-08-20T12:00Z";
        ZonedDateTime start = ZonedDateTime.parse(startUtc);
        ZonedDateTime end = ZonedDateTime.parse(endUtc);
        BillingPeriod expected = BillingPeriod.of(start, end);
        expected.setRenewalDays(Duration.ofDays(30));

        JSONObject input = new JSONObject().put("startDate", startUtc)
        .put("endDate", endUtc).put("autoRenew", true).put("renewalDays", 30L);

        assertEquals(expected, serializer.fromJson(input));
    }

    @Test
    void giveNullJsonShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class, () -> serializer.fromJson(null));
        assertEquals("billing period json must not be null", ex.getMessage());
    }
    
}
