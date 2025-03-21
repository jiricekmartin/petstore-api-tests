package dev.jiricekm.petstore.SecurityTests;
import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SecurityTests extends StoreApiTestBase {

    /**
     * This test verifies that the API is protected against SQL Injection attacks.
     * It attempts to inject malicious SQL code into the order ID field.
     */
    @Test
    public void testSQLInjectionOnPlaceOrder() {
        String maliciousPayload = "{\"id\":\"1; DROP TABLE orders;\"}";
        var response = StoreApiClient.placeOrderWithMalformedData(maliciousPayload);
        assertEquals(response.statusCode(), 400);
        assertEquals(response.jsonPath().getString("message"), "Invalid data format");
    }

    /**
     * This test verifies that unauthorized users are unable to delete orders.
     * It simulates an invalid authorization token in the request header.
     */
    @Test
    public void testUnauthorizedAccessOnDeleteOrder() {
        var response = StoreApiClient.attemptUnauthorizedOrderDeletion(6001);
        assertEquals(response.statusCode(), 403);
    }


    /**
     * This test verifies that the API is protected against Cross-Site Scripting (XSS) attacks.
     * It attempts to inject JavaScript code into the order status field.
     */
    @Test
    public void testXSSInjectionOnPlaceOrder() {
        String maliciousPayload = "{\"status\":\"<script>alert('XSS')</script>\"}";
        var response = StoreApiClient.placeOrderWithXSSPayload(maliciousPayload);
        assertEquals(response.statusCode(), 400);
        assertEquals(response.jsonPath().getString("message"), "Invalid data format");
    }

    /**
     * This test verifies that the API can reject extremely large payloads to prevent Denial-of-Service (DoS) attacks.
     * It submits a large JSON object with 10,000+ keys.
     */
    @Test
    public void testLargePayloadOnPlaceOrder() {
        StringBuilder largePayload = new StringBuilder("{");
        for (int i = 0; i < 10000; i++) {
            largePayload.append("\"key").append(i).append("\":\"value").append(i).append("\",");
        }
        largePayload.append("\"finalKey\":\"finalValue\"}");

        var response = StoreApiClient.placeOrderWithLargePayload(largePayload.toString());
        assertEquals(response.statusCode(), 413);
        assertEquals(response.jsonPath().getString("message"), "Payload too large");
    }

    /**
     * This test verifies that the API rejects unsupported content types.
     * The test sends data with 'text/xml' instead of 'application/json'.
     */
    @Test
    public void testInvalidContentType() {
        String invalidPayload = "<order><id>6002</id></order>";

        var response = StoreApiClient.placeOrderWithInvalidContentType(invalidPayload);
        assertEquals(response.statusCode(), 415);
        assertEquals(response.jsonPath().getString("message"), "Unsupported Media Type");
    }
}