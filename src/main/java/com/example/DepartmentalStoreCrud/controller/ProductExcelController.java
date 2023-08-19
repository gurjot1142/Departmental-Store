package com.example.DepartmentalStoreCrud.controller;


import com.example.DepartmentalStoreCrud.service.ProductExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/products")
public class ProductExcelController {

    /**
     * Autowired ProductExcelService
     */
    @Autowired
    private ProductExcelService productExcelService;

    /**
     * Uploads an Excel file containing product details and saves the data to the database.
     *
     * @param file The Excel file to upload.
     * @return A response entity indicating the status of the operation.
     */
    @Operation(operationId = "addProductDetailsViaExcel", summary = "Add Product Details Via Excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel file uploaded to db"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<String> uploadProductsViaExcel(@Parameter(description = "The Excel file to upload.", required = true)
                                                         @RequestParam("file") final MultipartFile file) throws IOException {
        productExcelService.addProductsViaExcel(file);
        return ResponseEntity.ok("Products added to database");
    }
}
