package io.github.pgmarc.space;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

@ExtendWith(MockServerExtension.class)
class MockServerTest {

    private static final String spaceOas = "https://raw.githubusercontent.com/Alex-GF/space/refs/heads/main/api/docs/space-api-docs.yaml";

    private final ClientAndServer client;

    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).build();

    public MockServerTest(ClientAndServer client) {
        this.client = client;
    }

    // TODO: Change field userId is defined as ObjectId which is false
    @Test
    void testOAS() {
        client.when(openAPI(spaceOas, "addContracts"))
         .respond(response().withBody("{'ping':'pong'}", MediaType.APPLICATION_JSON));

        JSONObject object = new JSONObject()
                .put("userContact", Map.of("username", "pgmarc", "userId", "68050bd09890322c57842f6f"))
                .put("billingPeriod", Map.of("autoRenew", true, "renewalDays", 365))
                .put("contractedServices", Map.of("zoom", "2025", "petclinic", "2024"))
                .put("subscriptionPlans", Map.of("zoom", "ENTERPRISE", "petclinic", "GOLD"))
                .put("subscriptionAddOns", Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + client.getPort().toString() + "/contracts"))
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("x-api-key", "prueba")
                .POST(BodyPublishers.ofString(object.toString())).build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JSONObject json = new JSONObject(response.body());
            assertEquals("pong", json.getString("ping"));
        } catch (IOException | InterruptedException e) {
            fail();
        }
    }

    @Test
    void testMockServer() {
        client.when(request().withMethod("GET").withPath("/test"))
                .respond(response().withBody("{'test':'foo'}", MediaType.APPLICATION_JSON));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + client.getPort().toString() + "/test"))
                .GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            assertEquals("foo", json.getString("test"));
        } catch (IOException | InterruptedException e) {
            fail();
        }

    }

}
