package com.example.DepartmentalStoreCrud.controller;

import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    /**
     * Autowired OrderService
     */
    @Autowired
    private OrderService orderService;

    /**
     * Retrieves all orders.
     *
     * @return List of orders.
     */
    @Operation(operationId = "getAllOrders", summary = "Get all Orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    /**
     * Retrieves an order by ID.
     *
     * @param orderID The ID of the order to retrieve.
     * @return The order with the specified ID.
     */
    @Operation(operationId = "getOrderByID", summary = "Get Order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{orderID}", produces = "application/json")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "The ID of the order to retrieve.", required = true)
            @PathVariable final Long orderID) {
        return ResponseEntity.ok(orderService.getOrderById(orderID));
    }

    /**
     * Adds a new order.
     *
     * @param order The order to add.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "addOrderDetails", summary = "Add Order Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addOrderDetails(@RequestBody(required = true) final Order order) {
        orderService.addOrderDetails(order);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully.");
    }

    /**
     * Updates an existing order.
     *
     * @param orderID The ID of the order to update.
     * @param order   The updated order.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "updateOrderDetails", summary = "Update Order Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(path = "/{orderID}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateOrderDetails(
            @Parameter(description = "The ID of the order to update.", required = true)
            @PathVariable final Long orderID, @RequestBody(required = true) final Order order) {
            orderService.updateOrderDetails(orderID, order);
            return ResponseEntity.ok("Order updated successfully.");
    }

    /**
     * Deletes an order by ID.
     *
     * @param orderID The ID of the order to delete.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "deleteOrder", summary = "Delete Order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(path = "/{orderID}")
    public ResponseEntity<String> deleteOrderDetails(
            @Parameter(description = "The ID of the order to delete.", required = true)
            @PathVariable final Long orderID) {
        orderService.deleteOrderDetails(orderID);
        return ResponseEntity.ok("Order deleted successfully.");
    }
}
