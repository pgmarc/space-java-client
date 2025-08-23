package io.github.pgmarc.space;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class ConfigTest {

    private static final String TEST_API_KEY = "fc851e857c18a5df8ef91dd8c63a1ca3";
    private static final String TEST_HOST = "example.com";

    @Test
    void givenRequiredParametersShouldCreateConfig() {

        Config config = Config.builder(TEST_HOST, TEST_API_KEY).build();

        assertAll(
                () -> assertThat(config.getUrl().toString()).isEqualTo("http://" + TEST_HOST + ":5403/api/v1"),
                () -> assertThat(config.getApiKey()).isEqualTo(TEST_API_KEY));

    }

    @Test
    void givenNoHostAndPortShouldThrow() {

        Config.Builder config1 = Config.builder(null, TEST_API_KEY);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> config1.build())
                .withMessage("host must not be null");
        Config.Builder config2 = Config.builder(TEST_HOST, null);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> config2.build())
                .withMessage("api key must not be null");

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

        assertAll(
                () -> assertThat(config.getUrl().toString())
                        .isEqualTo("http://" + TEST_HOST + ":" + port + "/" + prefixPath),
                () -> assertThat(config.getReadTimeout().toMillis()).isEqualTo(readTimeoutMillis),
                () -> assertThat(config.getWriteTimeout().toMillis()).isEqualTo(writeTimeoutMillis));

    }

    @Test
    void givenNullPathShouldUseDefaultPrefixPath() {

        Config config = Config.builder(TEST_HOST, TEST_API_KEY)
                .prefixPath(null)
                .build();
        assertThat(config.getUrl().toString()).isEqualTo("http://example.com:5403/api/v1");
    }

}
