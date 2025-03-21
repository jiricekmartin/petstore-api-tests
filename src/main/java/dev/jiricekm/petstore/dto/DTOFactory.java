package dev.jiricekm.petstore.dto;

import dev.jiricekm.petstore.enums.OrderStatus;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DTOFactory {

    private static final DateTimeFormatter SHIP_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /**
     * Creates an OrderDTO with dynamic ship date (current time + 1 day).
     */
    public static OrderDTO createOrder(long id, long petId, int quantity, OrderStatus status, boolean complete) {
        String shipDate = SHIP_DATE_FORMATTER.format(Instant.now().plus(1, ChronoUnit.DAYS));
        return new OrderDTO(id, petId, quantity, shipDate, status, complete);
    }

    /**
     * Creates an OrderDTO with a custom ship date.
     */
    public static OrderDTO createOrder(long id, long petId, int quantity, OrderStatus status, boolean complete, String shipDate) {
        return new OrderDTO(id, petId, quantity, shipDate, status, complete);
    }
}
