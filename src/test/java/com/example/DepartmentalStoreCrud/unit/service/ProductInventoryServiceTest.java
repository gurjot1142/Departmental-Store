package com.example.DepartmentalStoreCrud.unit.service;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import com.example.DepartmentalStoreCrud.service.ProductInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductInventoryServiceTest {

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @InjectMocks
    private ProductInventoryService productInventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        List<ProductInventory> products = new ArrayList<>();
        products.add(createProduct(1L)); // Sample product with ID 1L
        products.add(createProduct(2L));
        when(productInventoryRepository.findAll()).thenReturn(products);
        assertNotNull(products);
        List<ProductInventory> result = productInventoryService.getAllProducts();
        assertEquals(2, result.size());
        assertEquals(products, result);
        verify(productInventoryRepository).findAll();
    }

    @Test
    public void testGetProductById_ExistingProduct() {
        // Arrange
        Long productId = 1L;
        ProductInventory productInventory = createProduct(productId); // Sample order with ID 1L
        when(productInventoryRepository.findById(productId)).thenReturn(Optional.of(productInventory));

        //Argument Captor
        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        ProductInventory result = productInventoryService.getProductById(productId);

        // Assert
        assertEquals(productInventory, result);
        verify(productInventoryRepository, times(1)).findById(productIdCaptor.capture());
        assertEquals(productId, productIdCaptor.getValue());
    }

    @Test
    public void testGetProductById_NonExistingProduct() {
        // Arrange
        Long productId = 1L;
        when(productInventoryRepository.findById(productId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productInventoryService.getProductById(productId))
                .isInstanceOf(NoSuchElementException.class);
        verify(productInventoryRepository, times(1)).findById(productId);
    }

    @Test
    public void testAddProductDetails() {
        ProductInventory productInventory = createProduct(1L);
        when(productInventoryRepository.save(any(ProductInventory.class))).thenReturn(productInventory);

        // Argument captor
        ArgumentCaptor<ProductInventory> productCaptor = ArgumentCaptor.forClass(ProductInventory.class);


        ProductInventory newProduct = productInventoryService.addProductDetails(productInventory);
        assertNotNull(newProduct);
        assertEquals(productInventory, newProduct);
        verify(productInventoryRepository, times(1)).save(productCaptor.capture());
        assertEquals(productInventory, productCaptor.getValue());
    }

    @Test
    public void testAddProductDetails_NullInputValue() {
        ProductInventory productInventory = createProduct(1L);
        productInventory.setProductDesc(null);
        when(productInventoryRepository.save(any(ProductInventory.class))).thenReturn(productInventory);

        assertThatThrownBy(() -> productInventoryService.addProductDetails(productInventory))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productInventoryRepository, never()).save(productInventory);
    }

    @Test
    public void testUpdateProductDetails() {
        // Arrange
        ProductInventory product = createProduct(1L); // Sample product with ID 1L
        when(productInventoryRepository.findById(product.getProductID())).thenReturn(Optional.of(product));
        when(productInventoryRepository.save(product)).thenReturn(product);
        product.setProductName("Thar");

        //Argument captor
        ArgumentCaptor<ProductInventory> productCaptor = ArgumentCaptor.forClass(ProductInventory.class);

        // Act
        ProductInventory existingProduct = productInventoryService.updateProductDetails(product.getProductID(), product);
        assertNotNull(existingProduct);
        assertEquals("Thar", product.getProductName());

        // Assert
        verify(productInventoryRepository, times(1)).findById(product.getProductID());
        verify(productInventoryRepository, times(1)).save(productCaptor.capture());
        assertEquals(product, productCaptor.getValue());
    }

    @Test
    public void testUpdateProduct_NonExistingProduct() {
        // Arrange
        when(productInventoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        ProductInventory productInventory = new ProductInventory();
        assertThatThrownBy(() -> productInventoryService.updateProductDetails(anyLong(), productInventory))
                .isInstanceOf(NoSuchElementException.class);
        verify(productInventoryRepository, times(1)).findById(anyLong());
        verify(productInventoryRepository, never()).save(productInventory);
    }

    @Test
    public void testDeleteProductDetails_ExistingProduct() {
        // Arrange
        Long productId = 1L;
        ProductInventory productInventory = createProduct(productId);
        when(productInventoryRepository.findById(productId)).thenReturn(Optional.of(productInventory));

        //Argument Captor
        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        productInventoryService.deleteProductDetails(productId);

        // Assert
        verify(productInventoryRepository, times(1)).deleteById(productIdCaptor.capture());
        assertEquals(productId, productIdCaptor.getValue());
    }

    @Test
    public void testDeleteProductDetails_NonExistingProduct() {
        // Arrange
        Long productId = 3L;
        when(productInventoryRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> productInventoryService.deleteProductDetails(productId));

        // Assert
        verify(productInventoryRepository, times(1)).findById(productId);
        verify(productInventoryRepository, never()).delete(any());
    }

    @Test
    public void testSearchProducts_ResultFound() {
        List<ProductInventory> productInventory = new ArrayList<>();
        productInventory.add(createProduct(1L, "Furniture", "Office chair", 100.0, 10));
        productInventory.add(createProduct(2L, "Furniture", "Gaming chair", 100.0, 10));
        productInventory.add(createProduct(3L, "Jacket", "Puff Jacket", 100.0, 10));

        when(productInventoryRepository.findAll()).thenReturn(productInventory);
        assertNotNull(productInventory);
        List<ProductInventory> result = productInventoryService.searchProducts("Chair");
        assertEquals(2, result.size());
        assertEquals("Office chair", result.get(0).getProductName());
        assertEquals("Gaming chair", result.get(1).getProductName());
        verify(productInventoryRepository).findAll();
    }

    @Test
    public void testSearchProducts_NoResultsFound() {
        List<ProductInventory> productInventory = new ArrayList<>();
        productInventory.add(createProduct(1L, "Furniture", "Office chair", 100.0, 10));
        productInventory.add(createProduct(2L, "Furniture", "Gaming chair", 100.0, 10));
        productInventory.add(createProduct(3L, "Jacket", "Puff Jacket", 100.0, 10));

        when(productInventoryRepository.findAll()).thenReturn(productInventory);
        assertNotNull(productInventory);
        List<ProductInventory> result = productInventoryService.searchProducts("Mobile");
        assertTrue(result.isEmpty());
        verify(productInventoryRepository).findAll();
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
