package io.github.pgmarc.space.serializers;

import io.github.pgmarc.space.features.Consumption;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ConsumptionSerializerTest {

    private final ConsumptionSerializer serializer = new ConsumptionSerializer();

    @Test
    void givenConsumptionShouldSerialize() {

        String service = "Petclinic";
        int petsRegistered = 1;
        long fooConsumption = (long) Integer.MAX_VALUE + 1;
        float barConsumption = 0.1f;
        double consumedSeconds = (double) Float.MAX_VALUE + 1;

        Consumption consumption = Consumption.builder()
            .addInt(service, "maxPets", petsRegistered)
            .addLong(service, "fooLimit", fooConsumption)
            .addDouble(service, "maxSeconds", consumedSeconds)
            .addFloat(service, "barLimit", barConsumption)
            .build();

        assertThatNoException().isThrownBy(() -> serializer.toJson(consumption));

        String intUsageLimit = "petclinic-maxPets";
        String longUsageLimit = "petclinic-fooLimit";
        String floatUsageLimit = "petclinic-barLimit";
        String doubleUsageLimit =  "petclinic-maxSeconds";
        JSONObject consumptionPayload = serializer.toJson(consumption);

        assertThat(consumptionPayload.keySet()).contains(intUsageLimit, longUsageLimit, floatUsageLimit, doubleUsageLimit);
        assertThat(consumptionPayload.getInt(intUsageLimit)).isEqualTo(petsRegistered);
        assertThat(consumptionPayload.getLong(longUsageLimit)).isEqualTo(fooConsumption);
        assertThat(consumptionPayload.getFloat(floatUsageLimit)).isEqualTo(barConsumption);
        assertThat(consumptionPayload.getDouble(doubleUsageLimit)).isEqualTo(consumedSeconds);
    }

}
