package dev.jiricekm.petstore.FunctionalTests;

import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import dev.jiricekm.petstore.dto.DTOFactory;
import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.enums.OrderStatus;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DeleteOrderTests extends StoreApiTestBase {

    @Test
    public void testDeleteValidOrder() {
        OrderDTO order = DTOFactory.createOrder(3001, 10, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);

        StoreApiClient.deleteOrder(order.getId(), 200);
        StoreApiClient.getOrderById(order.getId(), 404);
    }

    @Test
    public void testDeleteNonExistentOrder() {
        long nonExistentOrderId = 99999;
        StoreApiClient.deleteOrder(nonExistentOrderId, 404);
    }

    @Test
    public void testDeleteOrderTwice() {
        OrderDTO order = DTOFactory.createOrder(3002, 10, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);

        StoreApiClient.deleteOrder(order.getId(), 200); // First deletion successful
        StoreApiClient.deleteOrder(order.getId(), 404); // Second deletion should fail
    }

    @Test
    public void testDeleteOrderWithInvalidId() {
        StoreApiClient.deleteOrder(-1, 404); // Invalid negative ID
        StoreApiClient.deleteOrder(0, 404);  // Zero as ID
        StoreApiClient.deleteOrder(Long.MAX_VALUE, 404); // Edge large number
    }

    @Test
    public void testDeleteMultipleOrders() {
        List<Long> orderIds = new ArrayList<>();
        for (int i = 3100; i <= 3102; i++) {
            OrderDTO order = DTOFactory.createOrder(i, 10, 1, OrderStatus.PLACED, true);
            StoreApiClient.placeOrder(order, 200);
            orderIds.add(order.getId());
        }

        for (Long orderId : orderIds) {
            StoreApiClient.deleteOrder(orderId, 200);
            StoreApiClient.getOrderById(orderId, 404);
        }
    }
}
