package dev.jiricekm.petstore.FunctionalTests;

import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import dev.jiricekm.petstore.dto.DTOFactory;
import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.enums.OrderStatus;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class OrderPlacementTests  extends StoreApiTestBase {

    @DataProvider(name = "placeOrderDataProvider")
    public Object[][] provideOrderData() {
        return new Object[][] {
                {DTOFactory.createOrder(101, 10, 1, OrderStatus.PLACED, true), 200},
                {DTOFactory.createOrder(102, 20, 1, OrderStatus.APPROVED, true), 200},
                {DTOFactory.createOrder(103, 30, 1, OrderStatus.DELIVERED, true), 200},
                {DTOFactory.createOrder(104, 10, 1, OrderStatus.PLACED, false), 200},
                {DTOFactory.createOrder(105, 20, 1, OrderStatus.APPROVED, false), 200},
                {DTOFactory.createOrder(106, 30, 1, OrderStatus.DELIVERED, false), 200}
        };
    }

    private List<Long> createdOrderIds = new ArrayList<>();

    @AfterClass
    public void cleanupOrders() {
        for (Long orderId : createdOrderIds) {
            StoreApiClient.deleteOrder(orderId, 200);
        }
    }

    @Test(dataProvider = "placeOrderDataProvider")
    public void testPlaceValidOrder(OrderDTO order, int expectedStatusCode) {
        Reporter.log("Testing placeOrder with order ID: " + order.getId());
        StoreApiClient.placeOrder(order, expectedStatusCode);
        createdOrderIds.add(order.getId());
    }

    @Test
    public void testPlaceOrderWithNegativeQuantity() {
        OrderDTO order = DTOFactory.createOrder(1002, 10, -5, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 400);
        createdOrderIds.add(order.getId());
    }

    @Test
    public void testPlaceOrderWithZeroQuantity() {
        OrderDTO order = DTOFactory.createOrder(1003, 10, 0, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 400);
    }

    @Test
    public void testPlaceOrderWithInvalidPetId() {
        OrderDTO order = DTOFactory.createOrder(1004, -1, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 400);
    }

    @Test
    public void testPlaceOrderWithLargeQuantity() {
        OrderDTO order = DTOFactory.createOrder(1005, 10, 10000, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);
        createdOrderIds.add(order.getId());

        OrderDTO fetchedOrder = StoreApiClient.getOrderById(order.getId());
        Assert.assertEquals(fetchedOrder.getQuantity(), 10000, "Large quantity mismatch.");
    }

    @Test
    public void testPlaceOrderWithInvalidStatus() {
        OrderDTO order = DTOFactory.createOrder(1007, 10, 2, null, true); // Invalid status
        StoreApiClient.placeOrder(order, 400);
    }

    @Test
    public void testPlaceMultipleValidOrders() {
        for (int i = 2001; i <= 2005; i++) {
            OrderDTO order = DTOFactory.createOrder(i, 10, 1, OrderStatus.PLACED, true);
            StoreApiClient.placeOrder(order, 200);
            Assert.assertNotNull(StoreApiClient.getOrderById(order.getId()));
            createdOrderIds.add(order.getId());
        }
    }
}