package io.github.pgmarc.space.serializers;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import io.github.pgmarc.space.contracts.UsageLevel;

final class UsageLevelDeserializer implements JsonDeserializable<Map<String, Map<String, UsageLevel>>> {

    @Override
    public Map<String, Map<String, UsageLevel>> fromJson(JSONObject usageLevel) {
        Objects.requireNonNull(usageLevel, "usage level must not be null");

        Map<String, Map<String, UsageLevel>> usageLevelMap = new HashMap<>();
        if (usageLevel.isEmpty()) {
            return usageLevelMap;
        }
        for (String serviceName : usageLevel.keySet()) {
            Map<String, UsageLevel> serviceLevels = new HashMap<>();
            JSONObject rawServiceUsageLevels = usageLevel.getJSONObject(serviceName);
            for (String usageLimitName : rawServiceUsageLevels.keySet()) {
                JSONObject rawUsageLevel = rawServiceUsageLevels.getJSONObject(usageLimitName);
                ZonedDateTime expirationDate = null;
                if (rawUsageLevel.has(UsageLevel.Keys.RESET_TIMESTAMP.toString())) {
                    expirationDate = ZonedDateTime
                            .parse(rawUsageLevel.getString(UsageLevel.Keys.RESET_TIMESTAMP.toString()));
                }
                UsageLevel ul = UsageLevel.of(serviceName, rawUsageLevel.getDouble(UsageLevel.Keys.CONSUMED.toString()),
                        expirationDate);
                serviceLevels.put(usageLimitName, ul);
            }
            usageLevelMap.put(serviceName, Collections.unmodifiableMap(serviceLevels));
        }
        return usageLevelMap;
    }
}
