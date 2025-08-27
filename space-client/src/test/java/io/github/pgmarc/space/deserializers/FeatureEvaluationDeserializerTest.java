package io.github.pgmarc.space.deserializers;

import io.github.pgmarc.space.exceptions.SpaceApiException;
import io.github.pgmarc.space.features.FeatureEvaluationResult;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class FeatureEvaluationDeserializerTest {

    private final FeatureEvaluationDeserializer deserializer = new FeatureEvaluationDeserializer();

    @Test
    void givenBooleanEvaluationResultShouldCreateObject() {

        JSONObject jsonObject = new JSONObject()
            .put("eval", true)
            .put("used", JSONObject.NULL)
            .put("limit", JSONObject.NULL)
            .put("error", JSONObject.NULL);

        assertThatNoException().isThrownBy(() -> deserializer.fromJson(jsonObject));
    }

    @Test
    void givenJsonShouldCreateEvaluationResult() {

        JSONObject jsonObject = new JSONObject()
            .put("eval", true)
            .put("used", Map.of("storage", 50, "apiCalls", 1, "bandwith", 20))
            .put("limit", Map.of("storage", 500, "apiCalls", 1000, "bandwith", 200))
            .put("error", JSONObject.NULL);

        assertThatNoException().isThrownBy(() -> deserializer.fromJson(jsonObject));

        FeatureEvaluationResult res =  deserializer.fromJson(jsonObject);
        assertThat(res.isAvailable()).isTrue();
        assertThat(res.getConsumed("storage")).isEqualTo(50);
        assertThat(res.getLimit("storage")).isEqualTo(500);
    }

    @Test
    void givenFeatureEvaluationErrorShouldThrow() {

        String code = "FLAG_NOT_FOUND";
        String message = "Feature pets not found in \"pricingContext\".";
        JSONObject jsonObject = new JSONObject()
            .put("eval", false)
            .put("used", JSONObject.NULL)
            .put("limit", JSONObject.NULL)
            .put("error", Map.of("code", code, "message", message));

        assertThatExceptionOfType(SpaceApiException.class)
            .isThrownBy(() -> deserializer.fromJson(jsonObject))
            .withMessage("Feature pets not found in \"pricingContext\". Error code: FLAG_NOT_FOUND");
    }
}
