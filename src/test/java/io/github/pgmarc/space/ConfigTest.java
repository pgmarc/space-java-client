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

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Config.builder(null, TEST_API_KEY).build());
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Config.builder(TEST_HOST, null).build());

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
