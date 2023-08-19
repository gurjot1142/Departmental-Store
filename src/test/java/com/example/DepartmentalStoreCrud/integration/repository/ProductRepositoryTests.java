package com.example.DepartmentalStoreCrud.integration.repository;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTests {
    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Test
    void testGetAllProducts() {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);
        assertEquals(2, productInventoryRepository.findAll().size());
    }

    @Test
    void testGetProductById() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        ProductInventory foundProduct = productInventoryRepository.findById(1L).orElse(null);
        assertNotNull(foundProduct);
        assertEquals("Product 1", product.getProductName());
        assertEquals("Description 1", product.getProductDesc());
        assertEquals(10.5, product.getPrice());
        assertEquals(100, product.getProductQuantity());
    }

    @Test
    void testSaveProduct() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10.5, 100);
        productInventoryRepository.save(product);
        ProductInventory foundProduct = productInventoryRepository.findById(1L).get();
        assertEquals(1, productInventoryRepository.findAll().size());
        assertNotNull(foundProduct);
        assertEquals("Product 1", foundProduct.getProductName());
        assertEquals("Description 1", foundProduct.getProductDesc());
        assertEquals(10.5, foundProduct.getPrice());
        assertEquals(100, foundProduct.getProductQuantity());
    }

    @Test
    void testDeleteProductById() {
        List<ProductInventory> productList = new ArrayList<>();
        productList.add(createProduct(1L, "Product 1", "Description 1", 10.5, 100));
        productList.add(createProduct(2L, "Product 2", "Description 2", 15.0, 50));
        productInventoryRepository.saveAll(productList);
        productInventoryRepository.deleteById(1L);
        assertEquals(1, productInventoryRepository.findAll().size());
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
