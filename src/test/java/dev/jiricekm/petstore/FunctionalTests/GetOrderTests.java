package dev.jiricekm.petstore.FunctionalTests;

import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import dev.jiricekm.petstore.dto.DTOFactory;
import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.enums.OrderStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GetOrderTests extends StoreApiTestBase {

    private final List<Long> createdOrderIds = new ArrayList<>();

    @AfterClass
    public void cleanupOrders() {
        for (Long orderId : createdOrderIds) {
            StoreApiClient.deleteOrder(orderId, 200);
        }
        createdOrderIds.clear();
    }

    @Test
    public void testGetValidOrder() {
        OrderDTO order = DTOFactory.createOrder(4001, 10, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);
        createdOrderIds.add(order.getId());

        OrderDTO fetchedOrder = StoreApiClient.getOrderById(order.getId());
        Assert.assertNotNull(fetchedOrder, "Order not found.");
        Assert.assertEquals(fetchedOrder.getId(), order.getId(), "Order ID mismatch.");
        Assert.assertEquals(fetchedOrder.getStatus(), order.getStatus(), "Order status mismatch.");
    }

    @Test
    public void testGetNonExistentOrder() {
        long nonExistentOrderId = 99999;
        StoreApiClient.getOrderById(nonExistentOrderId,404);
    }

    @Test
    public void testGetDeletedOrder() {
        OrderDTO order = DTOFactory.createOrder(4002, 10, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);

        StoreApiClient.deleteOrder(order.getId(), 200);
        StoreApiClient.getOrderById( order.getId(),404);
    }

    @Test
    public void testGetOrderWithInvalidId() {
        StoreApiClient.getOrderById( -1,404);
        StoreApiClient.getOrderById( 0,404);
        StoreApiClient.getOrderById( BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1)),404);
        StoreApiClient.getOrderById( BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1)),404);
    }

    @Test
    public void testGetMultipleValidOrders() {
        List<Long> orderIds = new ArrayList<>();
        for (int i = 4100; i <= 4102; i++) {
            OrderDTO order = DTOFactory.createOrder(i, 10, 1, OrderStatus.PLACED, true);
            StoreApiClient.placeOrder(order, 200);
            orderIds.add(order.getId());
        }

        for (Long orderId : orderIds) {
            OrderDTO fetchedOrder = StoreApiClient.getOrderById(orderId);
            Assert.assertNotNull(fetchedOrder, "Order with ID " + orderId + " not found.");
            Assert.assertEquals(fetchedOrder.getId(), orderId, "Order ID mismatch.");
        }

        createdOrderIds.addAll(orderIds);
    }
}
