package io.github.pgmarc.space;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class ConfigTest {

    private static final String TEST_API_KEY = "fc851e857c18a5df8ef91dd8c63a1ca3";
    private static final String TEST_HOST = "example.com";

    @Test
    void givenRequiredParametersShouldCreateConfig() {

        Config config = Config.builder(TEST_HOST, TEST_API_KEY).build();

        assertEquals("http://" + TEST_HOST + ":5403/api/v1", config.getUrl().toString());
        assertEquals(TEST_API_KEY, config.getApiKey());
    }

    @Test
    void givenNoHostAndPortShouldThrow() {

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> Config.builder(null, TEST_API_KEY).build()),
                () -> assertThrows(NullPointerException.class, () -> Config.builder(TEST_HOST, null).build()));
    }

    @Test
    void givenOptionalParemetersShoudCreate() {

        int port = 3000;
        String prefixPath = "api/v2";
        long writeTimeoutMillis = 700;
        long readTimeoutMillis = 500;

        Config config = Config.builder(TEST_HOST, TEST_API_KEY)
                .port(port)
                .prefixPath(prefixPath)
                .readTimeout(Duration.ofMillis(readTimeoutMillis))
                .writeTimeout(Duration.ofMillis(writeTimeoutMillis))
                .build();

        assertEquals("http://" + TEST_HOST + ":" + port + "/" + prefixPath, config.getUrl().toString());
        assertEquals(readTimeoutMillis, config.getReadTimeout().toMillis());
        assertEquals(writeTimeoutMillis, config.getWriteTimeout().toMillis());
    }

    @Test
    void givenNullPathShouldUseDefaultPrefixPath() {

        Config config = Config.builder(TEST_HOST, TEST_API_KEY)
                .prefixPath(null)
                .build();

        assertEquals("http://example.com:5403/api/v1", config.getUrl().toString());
    }

}
