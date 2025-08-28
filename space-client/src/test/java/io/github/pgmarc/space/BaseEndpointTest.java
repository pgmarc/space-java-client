package io.github.pgmarc.space;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class BaseEndpointTest {

    protected static final String TEST_API_KEY = "prueba";
    protected static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    protected static HttpUrl url;

    @RegisterExtension
    protected static WireMockExtension wm = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort().globalTemplating(true))
        .build();

    @BeforeAll
    static void setUp() {
        url = new HttpUrl.Builder().scheme("http").host("localhost").port(wm.getPort()).build();
    }


}
