package io.github.pgmarc.space.serializers;

import io.github.pgmarc.space.features.Consumption;
import org.json.JSONObject;

public final class ConsumptionSerializer implements JsonSerializable<Consumption> {

    private static String formatConsumptionKey(String serviceName, String usageLimitName) {
        return serviceName.toLowerCase() + "-" + usageLimitName;
    }

    @Override
    public JSONObject toJson(Consumption consumption) {

        JSONObject jsonObject = new JSONObject();
        for (Consumption.Item<?> item: consumption.getConsumption()) {
            jsonObject.put(formatConsumptionKey(item.getServiceName(), item.getUsageLimit()), item.getQuantity());
        }

        return jsonObject;
    }
}
