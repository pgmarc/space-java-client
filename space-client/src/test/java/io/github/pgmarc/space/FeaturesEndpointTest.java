package io.github.pgmarc.space;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.pgmarc.space.features.Consumption;
import io.github.pgmarc.space.features.FeatureEvaluationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.assertj.core.api.Assertions.*;

@WireMockTest
class FeaturesEndpointTest extends  BaseEndpointTest {

    private static FeaturesEndpoint endpoint;

    @BeforeAll
    static void setup() {
        endpoint = new FeaturesEndpoint(httpClient, url, TEST_API_KEY);
    }

    @Test
    void givenSimpleFeatureIdShouldEvaluate() {

        String userId = "e8e053c5-fd2b-4e4c-85a0-f1a52f0da72e";
        String featureId = "petclinic-featureA";

        wm.stubFor(post(urlPathTemplate("/features/{userId}/{featureId}"))
            .withHeader("x-api-key", equalTo("prueba"))
            .withPathParam("userId", equalTo(userId))
            .withPathParam("featureId", equalTo(featureId))
            .willReturn(
                ok()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("boolean-feature-evaluation.json")));

        try {
            FeatureEvaluationResult res = endpoint.evaluate(userId, "Petclinic", "featureA");
            assertThat(res.isAvailable()).isTrue();
            assertThat(res.getQuotas()).isEmpty();
        } catch (IOException e) {
            fail();
        }

    }

    @Test
    void givenConsumptionShouldEvaluateOptimistically() {

        String userId = "e8e053c5-fd2b-4e4c-85a0-f1a52f0da72e";
        String featureId = "petclinic-featureA";

        wm.stubFor(post(urlPathTemplate("/features/{userId}/{featureId}"))
            .withHeader("x-api-key", equalTo("prueba"))
            .withPathParam("userId", equalTo(userId))
            .withPathParam("featureId", equalTo(featureId))
            .willReturn(
                ok()
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("optimistic-evaluation-response.json")));

        String service = "Petclinic";
        String feature = "featureA";
        String usageLimit = "featureALimit";

        try {
            Consumption consumption = Consumption.builder().addInt(service, usageLimit, 100).build();
            FeatureEvaluationResult res = endpoint.evaluateOptimistically(userId, service, feature, consumption);
            assertThat(res.isAvailable()).isTrue();
            assertThat(res.getConsumed(usageLimit)).hasValue(100);
            assertThat(res.getLimit(usageLimit)).hasValue(500);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void getPricingTokenByUserId() {
    }
}
