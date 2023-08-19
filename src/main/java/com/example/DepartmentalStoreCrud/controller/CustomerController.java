package com.example.DepartmentalStoreCrud.controller;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(path = "/customers")
public class CustomerController {

    /**
     * Autowired CustomerService
     */
    @Autowired
    private CustomerService customerService;

    /**
     * Retrieves all customers.
     *
     * @return List of customers.
     */
    @Operation(operationId = "getAllCustomers", summary = "Get all Customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
    }

    /**
     * Retrieves a customer by ID.
     *
     * @param customerID The ID of the customer to retrieve.
     * @return The customer with the specified ID.
     */
    @Operation(operationId = "getCustomerByID", summary = "Get Customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{customerID}", produces = "application/json")
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "The ID of the customer to retrieve.", required = true)
            @PathVariable final Long customerID) {
        return new ResponseEntity<>(customerService.getCustomerById(customerID), HttpStatus.OK);
    }

    /**
     * Retrieves all orders placed by a customer.
     *
     * @param customerID The ID of the customer.
     * @return The orders placed by a customer.
     */
    @Operation(operationId = "getOrdersByCustomer", summary = "Get orders placed by a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "404", description = "No orders found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{customerID}/orders", produces = "application/json")
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @Parameter(description = "The ID of the customer required.", required = true)
            @PathVariable final Long customerID) {
        return new ResponseEntity<>(customerService.getOrdersByCustomer(customerID), HttpStatus.OK);
    }

    /**
     * Adds customer details.
     *
     * @param customer The customer details to add.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "addCustomerDetails", summary = "Add Customer Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer added successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addCustomerDetails(
            @RequestBody(required = true) final Customer customer) {
        customerService.addCustomerDetails(customer);
        return new ResponseEntity<>("Customer added successfully", HttpStatus.CREATED);
    }

    /**
     * Updates customer details by ID.
     *
     * @param customerID The ID of the customer to update.
     * @param customer   The updated customer details.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "updateCustomer", summary = "Update Customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(path = "/{customerID}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> updateCustomerDetails(
            @Parameter(description = "The ID of the customer to update.", required = true)
            @PathVariable final Long customerID,
            @RequestBody(required = true) final Customer customer) {
        customerService.updateCustomerDetails(customerID, customer);
        return new ResponseEntity<>("Customer updated successfully with id: " + customerID, HttpStatus.OK);
    }

    /**
     * Deletes a customer by ID.
     *
     * @param customerID The ID of the customer to delete.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "deleteCustomer", summary = "Delete Customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(path = "/{customerID}")
    public ResponseEntity<String> deleteCustomerDetails(
            @Parameter(description = "The ID of the customer to delete.", required = true)
            @PathVariable final Long customerID) {
        customerService.deleteCustomerDetails(customerID);
        return new ResponseEntity<>("Customer deleted successfully with id: " + customerID, HttpStatus.OK);
    }
}
