package com.example.DepartmentalStoreCrud.integration.service;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import com.example.DepartmentalStoreCrud.service.ProductInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Test
    void testGetAllProducts() {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);
        assertEquals(2, productInventoryService.getAllProducts().size());
    }

    @Test
    void testGetProductById_ProductFound() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        ProductInventory foundProduct = productInventoryService.getProductById(1L);
        assertNotNull(foundProduct);
        assertEquals("Product 1", foundProduct.getProductName());
        assertEquals("Description 1", foundProduct.getProductDesc());
        assertEquals(10.5, foundProduct.getPrice());
        assertEquals(100, foundProduct.getProductQuantity());
    }

    //negative case invalid product id
    @Test
    void testGetProductById_ProductNotFound() {
        assertThatThrownBy(() -> productInventoryService.getProductById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testAddProduct_Successful() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        ProductInventory newProduct = productInventoryService.addProductDetails(product);
        assertNotNull(newProduct);
        assertEquals("Product 1", newProduct.getProductName());
        assertEquals("Description 1", newProduct.getProductDesc());
        assertEquals(10.5, newProduct.getPrice());
        assertEquals(100, newProduct.getProductQuantity());
    }

    //negative case (invalid product details)
    @Test
    void testAddProduct_InvalidData() {
        ProductInventory product = createProduct(1L, null, "Description 1", 10.5, 100);
        assertThatThrownBy(() -> productInventoryService.addProductDetails(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateProductDetails_Successful() {
        productInventoryRepository.save(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        ProductInventory updatedProduct = productInventoryService.updateProductDetails(1L,
                createProduct(1L, "Product 1 Updated", "Description 1 Updated", 15.0, 200));
        assertEquals("Product 1 Updated", updatedProduct.getProductName());
        assertEquals("Description 1 Updated", updatedProduct.getProductDesc());
        assertEquals(15.0, updatedProduct.getPrice());
        assertEquals(200, updatedProduct.getProductQuantity());
    }

    //negative product not found
    @Test
    void testUpdateProductDetails_ProductNotFound() {
        ProductInventory product = new ProductInventory();
        assertThatThrownBy(() -> productInventoryService.updateProductDetails(2L, product))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testDeleteProductDetails_Successful() {
        productInventoryRepository.save(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productInventoryService.deleteProductDetails(1L);
        assertEquals(0, productInventoryService.getAllProducts().size());
    }

    //negative product not found
    @Test
    void testDeleteProductDetails_ProductNotFound() {
        assertThatThrownBy(() -> productInventoryService.deleteProductDetails(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testSearchProducts_Successful() {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);

        List<ProductInventory> searchResults = productInventoryService.searchProducts("Product 1");
        assertEquals(1, searchResults.size());
        assertEquals("Product 1", searchResults.get(0).getProductName());
    }

    //negative case (product not found in search)
    @Test
    void testSearchProducts_ProductNotFound() {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);

        List<ProductInventory> searchResults = productInventoryService.searchProducts("Product 3");
        assertEquals(0, searchResults.size());
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
