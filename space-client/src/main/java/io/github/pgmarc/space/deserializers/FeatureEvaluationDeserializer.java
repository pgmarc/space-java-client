package io.github.pgmarc.space.deserializers;

import io.github.pgmarc.space.exceptions.FeatureEvaluationError;
import io.github.pgmarc.space.exceptions.SpaceApiException;
import io.github.pgmarc.space.features.FeatureEvaluationResult;
import org.json.JSONObject;

import java.util.*;

public class FeatureEvaluationDeserializer implements JsonDeserializable<FeatureEvaluationResult> {

    private enum Keys {
        EVAL("eval"),
        ERROR("error"),
        CODE("code"),
        MESSAGE("message"),
        USED("used"),
        LIMIT("limit");

        private final String name;

        Keys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
           return name;
        }
    }

    @Override
    public FeatureEvaluationResult fromJson(JSONObject json) {

        JSONObject error = json.optJSONObject(Keys.ERROR.toString());
        if (error != null) {
            FeatureEvaluationError evalError = FeatureEvaluationError.of(error.getString(Keys.CODE.toString()),
                    error.getString(Keys.MESSAGE.toString()));
            throw new SpaceApiException(evalError.toString());
        }

        boolean available = json.getBoolean(Keys.EVAL.toString());
        JSONObject jsonUsed = json.optJSONObject(Keys.USED.toString());
        Map<String,Number> used = jsonUsed != null ? numberMapFromJson(jsonUsed) : Map.of();
        JSONObject jsonLimit =  json.optJSONObject(Keys.LIMIT.toString());
        Map<String,Number> limit = jsonLimit != null ? numberMapFromJson(jsonLimit) : Map.of();

        return FeatureEvaluationResult.of(available, used, limit);
    }

    private static Map<String,Number> numberMapFromJson(JSONObject jsonObject) {
        Map<String,Number> res = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            res.put(key, jsonObject.getNumber(key));
        }
        return res;
    }
}
