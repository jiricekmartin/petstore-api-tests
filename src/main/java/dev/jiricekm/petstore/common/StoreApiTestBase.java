package dev.jiricekm.petstore.common;

import dev.jiricekm.petstore.Config;
import dev.jiricekm.petstore.listeners.TestListener;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class StoreApiTestBase {
    @BeforeClass
    protected void setup() {
        RestAssured.baseURI = Config.getBaseUri();

        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 20000)
                        .setParam("http.socket.timeout", 20000));

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
