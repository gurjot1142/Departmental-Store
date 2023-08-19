package com.example.DepartmentalStoreCrud.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductInventoryService {

    /**
     * Autowired ProductInventoryRepository
     */
    @Autowired
    private ProductInventoryRepository productRepo;

    /**
     * Autowired BackorderRepository
     */
    @Autowired
    private BackorderRepository backorderRepo;

    /**
     * Autowired OrderRepository
     */
    @Autowired
    private OrderRepository orderRepo;

    /**
     * To get List of all Products.
     *
     * @return List of Products
     *
     */
    public List<ProductInventory> getAllProducts() {
        log.info("Products list fetched");
        return productRepo.findAll();
    }

    /**
     * To get product list in pages.
     *
     * @param pageNumber The number of pages
     * @param pageSize The size of each page
     * @return Pages of products
     */
    public List<ProductInventory> getProductsPagination(final Integer pageNumber, final Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductInventory> pageProduct = productRepo.findAll(pageable);
        log.info("Pagination done");
        return pageProduct.getContent();
    }

    /**
     * To get Details of Product with Product's ID.
     *
     * @param productID - Product's ID
     * @return Product Details
     */
    public ProductInventory getProductById(final Long productID) {
        Optional<ProductInventory> productInventory = productRepo.findById(productID);
        if (productInventory.isEmpty()) {
            log.info("Invalid product id");
            throw new NoSuchElementException("No product exists with ID: " + productID);
        }
        log.info("Product found with id-" + productID);
        return productInventory.get();
    }

    /**
     * To Add a new Product.
     *
     * @param productInventory - Product's details
     * @return Product's Details
     */
    public ProductInventory addProductDetails(final ProductInventory productInventory) {
        if (productInventory.getProductName() == null || productInventory.getProductDesc() == null) {
            log.info("Product details are missing");
            throw new IllegalArgumentException("Enter valid product data");
        }
        log.info("Product added successfully");
        return productRepo.save(productInventory);
    }

    /**
     * To update Product details.
     *
     * @param productInventory - Product's details
     * @return Product's Details
     */
    public ProductInventory updateProductDetails(final Long productID, final ProductInventory productInventory) {
        Optional<ProductInventory> productInventoryOptional = productRepo.findById(productID);
        if (productInventoryOptional.isEmpty()) {
            log.info("Invalid product id");
            throw new NoSuchElementException("No product exists with ID: " + productID);
        }
        ProductInventory existingProduct = productInventoryOptional.get();
        existingProduct.setProductDesc(productInventory.getProductDesc() == null ? existingProduct.getProductDesc() : productInventory.getProductDesc());
        existingProduct.setProductName(productInventory.getProductName() == null ? existingProduct.getProductName() : productInventory.getProductName());
        existingProduct.setPrice(productInventory.getPrice());
        existingProduct.setProductQuantity(productInventory.getProductQuantity());
        log.info("Product updated successfully with id-" + productID);
        return productRepo.save(existingProduct);
    }

    /**
     * To remove the backorders for a product
     *
     * @param newQuantity - Product's updated quantity
     * @param existingProduct - Product details
     */
    private void removeBackorders(final int newQuantity, final ProductInventory existingProduct) {
        if (newQuantity > 0) {
            List<Order> orders = orderRepo.findByProductInventory(existingProduct);
            if (!orders.isEmpty()) {
                for (Order order : orders) {
                    // Remove the backorder if quantity becomes sufficient
                    if (existingProduct.getProductQuantity() >= order.getOrderQuantity()) {
                        Backorder backorder = backorderRepo.findByOrder(order);
                        if (backorder != null) {
                                // Remove the backorder associated with the order
                                backorderRepo.delete(backorder);
                                existingProduct.setProductQuantity(existingProduct.getProductQuantity() - order.getOrderQuantity());
                        }
                }
            }
            productRepo.save(existingProduct);
        }
      }
    }

    /**
     * Cronjob to run the removeBackorders function every midnight
     */
    @Scheduled(cron = "0 0 0 * * *")   // Runs every midnight
    private void deleteBackordersCronJob() {
        // Get all existing products
        List<ProductInventory> products = productRepo.findAll();
        for (ProductInventory existingProduct : products) {
            int quantity = existingProduct.getProductQuantity();
            removeBackorders(quantity, existingProduct);
        }
        log.info("Cronjob successful");
    }

    /**
     * To delete/remove the product.
     *
     * @param productID - Product's ID
     */
    public void deleteProductDetails(final Long productID) {
        Optional<ProductInventory> productInventory = productRepo.findById(productID);
        if (productInventory.isEmpty()) {
            log.info("Invalid product id");
            throw new NoSuchElementException("No product exists with ID: " + productID);
        }
        log.info("Product deleted with id-" + productID);
        productRepo.deleteById(productID);
    }

    /**
     * To search for a product by its name.
     *
     * @param productName - Name of the product
     * @return List of products
     */
    public List<ProductInventory> searchProducts(final String productName) {
        List<ProductInventory> products = productRepo.findAll();
        log.info("Search successful");
        return products.stream()
                .filter(product -> product.getProductName().toLowerCase().contains(productName.toLowerCase()))
                .collect(Collectors.toList());
    }
}
