package dev.jiricekm.petstore.common;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.helpers.StoreOrderHelper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.Reporter;

import java.math.BigInteger;
import java.util.Map;

public class StoreApiClient {
    static final String BASE_URL = "/store";

    private static RequestSpecification getRequest() {
        return given().contentType(ContentType.JSON).log().all();
    }

    public static OrderDTO placeOrder(OrderDTO order, int expectedStatusCode) {
        Reporter.log("Placing order with ID: " + order.getId());
        String response = getRequest()
                .body(StoreOrderHelper.createOrderJson(order))
                .when()
                .post(BASE_URL + "/order")
                .then().log().ifError()
                .statusCode(expectedStatusCode)
                .extract().asString();

        return StoreOrderHelper.parseOrderJson(response);
    }

    public static void deleteOrder(long orderId, int expectedStatusCode) {
        Reporter.log("Deleting order with ID: " + orderId);
        Response response = getRequest()
                .when()
                .delete(BASE_URL + "/order/" + orderId)
                .then().log().ifError()
                .statusCode(expectedStatusCode)
                .extract().response();

        Reporter.log("Delete response: " + response.asString());
    }

    public static Map<String, Integer> getInventory() {
        Reporter.log("Fetching inventory details.");

        Map<String, Integer> inventory = getRequest()
                .when()
                .get(BASE_URL + "/inventory")
                .then()
                .statusCode(200)
                .extract().jsonPath().getMap("$");

        Reporter.log("Inventory Response: " + inventory);
        return inventory;
    }

    public static OrderDTO getOrderById(long orderId) {
        return getOrderById(orderId, 200);
    }

    public static OrderDTO getOrderById(long orderId, Integer statusCode) {
        Reporter.log("Fetching order with ID: " + orderId);
        Response response = getRequest()
                .when()
                .get(BASE_URL + "/order/" + orderId)
                .then().log().ifError()
                .statusCode(statusCode)
                .extract().response();

        if (response.statusCode() != statusCode) {
            throw new AssertionError("Unexpected response: " + response.getBody().asString());
        }

        return StoreOrderHelper.parseOrderJson(response.asString());
    }

    public static void getOrderById(BigInteger orderId, int expectedStatusCode) {
        Reporter.log("Fetching order with ID: " + orderId);
        getRequest()
                .when()
                .get(BASE_URL + "/order/" + orderId)
                .then()
                .statusCode(expectedStatusCode);
    }

    public static void placeOrder(OrderDTO order, int expectedStatusCode, Long maxResponseTime) {
        RequestSpecification request = getRequest().body(StoreOrderHelper.createOrderJson(order));

        ValidatableResponse response = (maxResponseTime != null)
                ? request.when().post("/store/order").then().time(lessThan(maxResponseTime))
                : request.when().post("/store/order").then();

        response.statusCode(expectedStatusCode);
    }

    public static void getOrder(long orderId, long maxResponseTime) {
        getRequest()
                .when()
                .get("/store/order/" + orderId)
                .then()
                .statusCode(200)
                .time(lessThan(maxResponseTime));
    }

    public static void deleteOrder(long orderId, long maxResponseTime) {
        getRequest()
                .when()
                .delete("/store/order/" + orderId)
                .then()
                .statusCode(200)
                .time(lessThan(maxResponseTime));
    }

    public static void getInventory(long maxResponseTime) {
        getRequest()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .time(lessThan(maxResponseTime));
    }

    public static Response attemptUnauthorizedOrderDeletion(long orderId) {
        Reporter.log("Attempting unauthorized deletion for Order ID: " + orderId);
        return given()
                .header("Authorization", "InvalidToken")
                .when()
                .delete(BASE_URL + "/order/" + orderId)
                .then().log().ifError()
                .extract().response();
    }

    public static Response placeOrderWithMalformedData(String maliciousPayload) {
        Reporter.log("Placing order with potential SQL Injection payload.");
        return given()
                .contentType(ContentType.JSON)
                .body(maliciousPayload)
                .when()
                .post(BASE_URL + "/order")
                .then().log().ifError()
                .extract().response();
    }

    public static Response placeOrderWithLargePayload(String largePayload) {
        Reporter.log("Placing order with large payload for DoS testing.");
        return given()
                .contentType(ContentType.JSON)
                .body(largePayload)
                .when()
                .post(BASE_URL + "/order")
                .then().log().ifError()
                .extract().response();
    }

    public static Response placeOrderWithInvalidContentType(String invalidPayload) {
        Reporter.log("Placing order with invalid content type.");
        return given()
                .contentType("text/xml")
                .body(invalidPayload)
                .when()
                .post(BASE_URL + "/order")
                .then().log().ifError()
                .extract().response();
    }

    public static Response placeOrderWithXSSPayload(String xssPayload) {
        Reporter.log("Placing order with potential XSS payload.");
        return given()
                .contentType(ContentType.JSON)
                .body(xssPayload)
                .when()
                .post(BASE_URL + "/order")
                .then().log().ifError()
                .extract().response();
    }
}