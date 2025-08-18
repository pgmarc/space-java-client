package io.github.pgmarc.space;

import java.time.Duration;
import java.util.Objects;

import okhttp3.HttpUrl;

public final class Config {

    private static final String DEFAULT_SCHEME = "http";

    private final String apiKey;
    private final String host;
    private final int port;
    private final String prefixPath;
    private final Duration readTimeout;
    private final Duration writeTimeout;

    private Config(Builder builder) {
        this.host = builder.host;
        this.apiKey = builder.apiKey;
        this.port = builder.port;
        this.prefixPath = builder.prefixPath;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public HttpUrl getUrl() {
        return new HttpUrl.Builder().scheme(DEFAULT_SCHEME)
                .host(this.host).port(this.port)
                .addPathSegments(prefixPath).build();
    }

    public static Builder builder(String host, String apiKey) {
        return new Builder(host, apiKey);
    }

    public static final class Builder {

        private final String host;
        private final String apiKey;
        private int port = 5403;
        private String prefixPath = "api/v1";
        private Duration readTimeout;
        private Duration writeTimeout;

        private Builder(String host, String apiKey) {
            this.host = host;
            this.apiKey = apiKey;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder readTimeout(Duration duration) {
            if (duration != null) {
                this.readTimeout = duration;
            }
            return this;
        }

        public Builder writeTimeout(Duration duration) {
            if (duration != null) {
                this.writeTimeout = duration;
            }
            return this;
        }

        public Builder prefixPath(String prefixPath) {
            if (prefixPath != null) {
                this.prefixPath = prefixPath;
            }
            return this;
        }

        public Config build() {
            Objects.requireNonNull(this.host, "host must not be null");
            Objects.requireNonNull(this.apiKey, "api key must not be null");
            return new Config(this);
        }

    }

}
