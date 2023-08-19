package com.example.DepartmentalStoreCrud.integration.controller;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Test
    void testGetAllProducts() throws Exception {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(productList.size())));
    }

    @Test
    void testGetProductById_Successful() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productName").value("Product 1"))
                .andExpect(jsonPath("$.productDesc").value("Description 1"))
                .andExpect(jsonPath("$.price").value(10.5))
                .andExpect(jsonPath("$.productQuantity").value(100));
    }

    //negative case invalid product id
    @Test
    void testGetProductById_ProductNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productID}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddProduct_Successful() throws Exception {
        String productData = "{ \"productID\": 1, \"productName\": \"Product 1\", \"productDesc\": \"Description 1\", \"price\": 10.5, \"productQuantity\": 100 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productData))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully."));
    }

    @Test
    void testUpdateProduct_Successful() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        String updateProduct = "{ \"productID\": 1, \"productName\": \"Product 1 Updated\", \"productDesc\": \"Description 1 Updated\", \"price\": 15.0, \"productQuantity\": 200 }";

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{productID}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateProduct))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully."));
    }

    //negative product not found
    @Test
    void testUpdateProduct_ProductNotFound() throws Exception {
        String updateProduct = "{ \"productID\": 1, \"productName\": \"Product 1\", \"productDesc\": \"Description 1\", \"price\": 10.5, \"productQuantity\": 100 }";
        mockMvc.perform(MockMvcRequestBuilders.put("/products/{productID}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateProduct))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_Successful() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{productID}", 1L))
                .andExpect(status().isOk());
    }

    //negative product not found
    @Test
    void testDeleteProduct_ProductNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{productID}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchProducts_Successful() throws Exception {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/search")
                        .param("name", "Product 1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].productName").value("Product 1"));
    }

    //negative case (product not found in search)
    @Test
    void testSearchProducts_ProductNotFound() throws Exception {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/search")
                        .param("name", "Product 3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(0)));
    }

    private ProductInventory createProduct(Long id, String name, String desc, double price, int quantity) {
        ProductInventory product = new ProductInventory();
        product.setProductID(id);
        product.setProductName(name);
        product.setProductDesc(desc);
        product.setPrice(price);
        product.setProductQuantity(quantity);
        return product;
    }
}
