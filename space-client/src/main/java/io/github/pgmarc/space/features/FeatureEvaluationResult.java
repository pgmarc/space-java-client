package io.github.pgmarc.space.features;

import java.util.Collections;
import java.util.Map;

public final class FeatureEvaluationResult {

    private final boolean available;
    private final Map<String, Number> used;
    private final Map<String, Number> limit;

    private FeatureEvaluationResult(boolean available, Map<String,Number> used, Map<String,Number> limit) {
        this.available = available;
        this.used = Collections.unmodifiableMap(used);
        this.limit = Collections.unmodifiableMap(limit);
    }

    public boolean isAvailable() {
        return available;
    }

    public Number getConsumed(String usageLimit) {
        return used.get(usageLimit);
    }

    public Number getLimit(String usageLimit) {
        return limit.get(usageLimit);
    }

    public static FeatureEvaluationResult of(boolean available, Map<String,Number> used, Map<String,Number> limit) {
        return new FeatureEvaluationResult(available, used, limit);
    }

}
