package dev.jiricekm.petstore.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.jiricekm.petstore.dto.OrderDTO;

import static dev.jiricekm.petstore.Config.isPrettyPrintEnabled;

public class StoreOrderHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        if (isPrettyPrintEnabled()) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Serializes an OrderDTO object to JSON string.
     *
     * @param order The OrderDTO object to serialize.
     * @return JSON string representation of the order.
     */
    public static String createOrderJson(OrderDTO order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        try {
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes a JSON string into an OrderDTO object.
     *
     * @param json The JSON string to deserialize.
     * @return The corresponding OrderDTO object.
     */
    public static OrderDTO parseOrderJson(String json) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return objectMapper.readValue(json, OrderDTO.class);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("Invalid JSON format for OrderDTO: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize order: " + e.getMessage(), e);
        }
    }
}