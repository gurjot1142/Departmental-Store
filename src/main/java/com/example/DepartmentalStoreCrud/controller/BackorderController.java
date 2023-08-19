package com.example.DepartmentalStoreCrud.controller;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.service.BackorderService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/backorders")
public class BackorderController {

    /**
     * Autowired BackorderService
     */
    @Autowired
    private BackorderService backorderService;

    /**
     * Creates a new backorder.
     *
     * @param backorder The backorder to create.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "createBackorder", summary = "Create a new Backorder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Backorder created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createBackorder(@RequestBody(required = true) final Backorder backorder) {
        return ResponseEntity.status(HttpStatus.CREATED).body("Backorder created successfully.");
    }

    /**
     * Retrieves all backorders.
     *
     * @return List of backorders.
     */
    @Operation(operationId = "getAllBackorders", summary = "Get all Backorders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backorders fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Backorder>> getAllBackorders() {
        return new ResponseEntity<>(backorderService.getAllBackorders(), HttpStatus.OK);
    }

    /**
     * Retrieves a backorder by ID.
     *
     * @param backorderId The ID of the backorder to retrieve.
     * @return A response entity containing the retrieved backorder.
     */
    @Operation(operationId = "getBackorderByID", summary = "Get Backorder by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backorder found"),
            @ApiResponse(responseCode = "404", description = "Backorder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{backorderId}", produces = "application/json")
    public ResponseEntity<Backorder> getBackorderById(
            @Parameter(description = "The ID of the backorder to retrieve.", required = true)
            @PathVariable final Long backorderId) {
        return ResponseEntity.ok(backorderService.getBackorderById(backorderId));
    }

    /**
     * Deletes a backorder by ID.
     *
     * @param backorderId The ID of the backorder to delete.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "deleteBackorder", summary = "Delete Backorder by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backorder deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Backorder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(path = "/{backorderId}")
    public ResponseEntity<String> deleteBackorder(
            @Parameter(description = "The ID of the backorder to delete.", required = true)
            @PathVariable final Long backorderId) {
        backorderService.deleteBackorder(backorderId);
        return ResponseEntity.ok("Backorder deleted successfully.");
    }
}
