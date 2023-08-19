package com.example.DepartmentalStoreCrud.unit.controller;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.controller.ProductInventoryController;
import com.example.DepartmentalStoreCrud.service.ProductInventoryService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = AutoConfigureMockMvc.class)
@WebMvcTest(ProductInventoryController.class)
public class ProductInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductInventoryService productInventoryService;

    @InjectMocks
    private ProductInventoryController productInventoryController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(productInventoryController).build();
    }

    @Test
    void getAllProductsTest() throws Exception {
        List<ProductInventory> products = new ArrayList<>();
        products.add(createProduct(1L));
        products.add(createProduct(2L));

        when(productInventoryService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(products.size())));

        verify(productInventoryService, times(1)).getAllProducts();
    }

    @Test
    void getProductByIdTest() throws Exception {
        Long productId = 1L;
        ProductInventory product = createProduct(productId);

        when(productInventoryService.getProductById(productId)).thenReturn(product);

        mockMvc.perform(get("/products/{productID}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productID", is(product.getProductID().intValue())))
                .andExpect(jsonPath("$.productName", is(product.getProductName())))
                .andExpect(jsonPath("$.productQuantity", is(product.getProductQuantity())))
                .andExpect(jsonPath("$.price", is(product.getPrice())));

        verify(productInventoryService, times(1)).getProductById(productId);
    }

    //negative case
    @Test
    void getProductByIdTest_ProductNotFound() throws Exception {
        Long nonExistentProductId = 999L;

        when(productInventoryService.getProductById(nonExistentProductId)).thenReturn(null);

        mockMvc.perform(get("/{productID}", nonExistentProductId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addProductDetailsTest() throws Exception {
        ProductInventory product = createProduct(1L);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully."));

        verify(productInventoryService, times(1)).addProductDetails(product);
    }

    @Test
    void updateProductDetailsTest() throws Exception {
        Long productId = 1L;
        ProductInventory product = createProduct(productId);

        mockMvc.perform(put("/products/{productID}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully."));

        verify(productInventoryService, times(1)).updateProductDetails(eq(productId), any(ProductInventory.class));
    }

    //negative case
    @Test
    void updateProductDetailsTest_NonExistentProduct() throws Exception {
        Long nonExistentProductId = 999L;
        ProductInventory product = createProduct(nonExistentProductId);

        when(productInventoryService.updateProductDetails(nonExistentProductId, product)).thenReturn(null);

        mockMvc.perform(put("/{productID}", nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());

        verify(productInventoryService, never()).updateProductDetails(nonExistentProductId, product);
    }

    @Test
    void deleteProductDetailsTest() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/products/{productID}", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));

        verify(productInventoryService, times(1)).deleteProductDetails(productId);
    }

    //negative case
    @Test
    void deleteProductDetailsTest_NonExistentProduct() throws Exception {
        Long nonExistentProductId = 999L;

        doNothing().when(productInventoryService).deleteProductDetails(nonExistentProductId);

        mockMvc.perform(delete("/{productID}", nonExistentProductId))
                .andExpect(status().isNotFound());

        verify(productInventoryService, never()).deleteProductDetails(nonExistentProductId);
    }

    @Test
    void testSearchProducts() throws Exception {
        List<ProductInventory> productInventory = new ArrayList<>();
        productInventory.add(createProduct(1L, "Furniture", "Office chair", 100.0, 10));
        productInventory.add(createProduct(2L, "Furniture", "Gaming chair", 100.0, 10));
        productInventory.add(createProduct(3L, "Jacket", "Puff Jacket", 100.0, 10));

        List<ProductInventory> result = new ArrayList<>();
        result.add(productInventory.get(0));
        result.add(productInventory.get(1));
        when(productInventoryService.searchProducts("chair")).thenReturn(result);
        mockMvc.perform(get("/products/search")
                        .param("name", "chair"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productName", is("Office chair")))
                .andExpect(jsonPath("$[1].productName", is("Gaming chair")));

        verify(productInventoryService, times(1)).searchProducts("chair");
    }

    private ProductInventory createProduct(Long productId) {
        ProductInventory product = new ProductInventory();
        product.setProductID(productId);
        product.setProductDesc("Product description");
        product.setProductName("Product name");
        product.setPrice(9.99); // Set the desired price
        product.setProductQuantity(10); // Set the desired count

        return product;
    }

    private ProductInventory createProduct(Long productId, String productDesc, String productName, double price, int productQuantity) {
        ProductInventory product = new ProductInventory();
        product.setProductID(productId);
        product.setProductDesc(productDesc);
        product.setProductName(productName);
        product.setPrice(price);
        product.setProductQuantity(productQuantity);
        return product;
    }
}
