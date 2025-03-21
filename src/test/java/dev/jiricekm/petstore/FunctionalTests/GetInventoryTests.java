package dev.jiricekm.petstore.FunctionalTests;

import dev.jiricekm.petstore.common.StoreApiClient;
import dev.jiricekm.petstore.common.StoreApiTestBase;
import dev.jiricekm.petstore.dto.DTOFactory;
import dev.jiricekm.petstore.dto.OrderDTO;
import dev.jiricekm.petstore.enums.OrderStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class GetInventoryTests extends StoreApiTestBase {
    @Test
    public void testGetInventorySuccess() {
        Map<String, Integer> inventory = StoreApiClient.getInventory();
        Assert.assertNotNull(inventory, "Inventory response is null.");
        Assert.assertFalse(inventory.isEmpty(), "Inventory is empty.");
    }

    @Test
    public void testInventoryContainsKeyAvailable() {
        Map<String, Integer> inventory = StoreApiClient.getInventory();
        Assert.assertTrue(inventory.containsKey("available"), "Inventory does not contain 'available'.");
    }

    @Test
    public void testInventoryCountsArePositive() {
        Map<String, Integer> inventory = StoreApiClient.getInventory();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            Assert.assertTrue(entry.getValue() >= 0, "Inventory count for '" + entry.getKey() + "' is negative.");
        }
    }

    @Test
    public void testInventoryContainsMultipleStatuses() {
        Map<String, Integer> inventory = StoreApiClient.getInventory();
        Assert.assertTrue(inventory.containsKey("available"), "Inventory does not contain 'available'.");
        Assert.assertTrue(inventory.containsKey("pending"), "Inventory does not contain 'pending'.");
        Assert.assertTrue(inventory.containsKey("sold"), "Inventory does not contain 'sold'.");
    }

    @Test
    public void testInventoryForConsistentData() {
        Map<String, Integer> initialInventory = StoreApiClient.getInventory();

        OrderDTO order = DTOFactory.createOrder(5001, 10, 1, OrderStatus.PLACED, true);
        StoreApiClient.placeOrder(order, 200);

        Map<String, Integer> updatedInventory = StoreApiClient.getInventory();
        Assert.assertEquals((int) updatedInventory.get("available"),
                initialInventory.get("available") - 1,
                "Inventory count did not decrease after placing an order.");

        StoreApiClient.deleteOrder(order.getId(), 200);
        Map<String, Integer> restoredInventory = StoreApiClient.getInventory();
        Assert.assertEquals((int) restoredInventory.get("available"),
                (int) initialInventory.get("available"),
                "Inventory count did not restore after order deletion.");
    }
}
