package dev.jiricekm.petstore.PerformanceTests;

import dev.jiricekm.petstore.Config;
import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import dev.jiricekm.petstore.dto.DTOFactory;
import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.enums.OrderStatus;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static dev.jiricekm.petstore.common.StoreApiClient.deleteOrder;
import static dev.jiricekm.petstore.common.StoreApiClient.getInventory;
import static dev.jiricekm.petstore.common.StoreApiClient.placeOrder;
import static dev.jiricekm.petstore.common.StoreApiClient.getOrder;


public class PerformanceTests extends StoreApiTestBase {

    private static final long MAX_PLACE_ORDER_RESPONSE_TIME = Config.getLongProperty("max.placeOrder.response.time", 2000L);
    private static final long MAX_GET_ORDER_RESPONSE_TIME = Config.getLongProperty("max.getOrder.response.time", 1500L);
    private static final long MAX_DELETE_ORDER_RESPONSE_TIME = Config.getLongProperty("max.deleteOrder.response.time", 1000L);
    private static final long MAX_INVENTORY_RESPONSE_TIME = Config.getLongProperty("max.inventory.response.time", 2000L);
    private static final long MAX_UNDER_LOAD_RESPONSE_TIME = Config.getLongProperty("max.under.load.response.time", 3000L);

    private static final int THREAD_COUNT = Config.getIntProperty("load.test.threads", 50);
    private static final int TOTAL_REQUESTS = Config.getIntProperty("load.test.requests", 200);

    @Test
    public void testPlaceOrderPerformance() {
        OrderDTO order = DTOFactory.createOrder(6001, 10, 1, OrderStatus.PLACED, true);
        placeOrder(order, 200, MAX_PLACE_ORDER_RESPONSE_TIME);
    }

    @Test
    public void testGetOrderPerformance() {
        getOrder(6001, MAX_GET_ORDER_RESPONSE_TIME);
    }

    @Test
    public void testDeleteOrderPerformance() {
        deleteOrder(6001, MAX_DELETE_ORDER_RESPONSE_TIME);
    }

    @Test
    public void testGetInventoryPerformance() {
        getInventory(MAX_INVENTORY_RESPONSE_TIME);
    }

    @Test
    public void testPlaceOrderUnderLoad() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                int orderId = 7000 + i;
                executor.submit(() -> {
                    OrderDTO order = DTOFactory.createOrder(orderId, 10, 1, OrderStatus.PLACED, true);
                    StoreApiClient.placeOrder(order, 200, MAX_UNDER_LOAD_RESPONSE_TIME);
                });
            }
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                Reporter.log("Some tasks were not completed within the expected time frame.");
            }
        }
    }

    @Test
    public void testGetInventoryUnderLoad() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                executor.submit(() -> {
                    StoreApiClient.getInventory(MAX_UNDER_LOAD_RESPONSE_TIME);
                });
            }
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                Reporter.log("Some tasks were not completed within the expected time frame.");
            }
        }
    }
}
