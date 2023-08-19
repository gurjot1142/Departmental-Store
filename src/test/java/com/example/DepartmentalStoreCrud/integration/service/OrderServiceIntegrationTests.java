package com.example.DepartmentalStoreCrud.integration.service;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import com.example.DepartmentalStoreCrud.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceIntegrationTests {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void testGetAllOrders() {
        ProductInventory product1 = createProduct(1L, "Product 1", "Description 1", 10, 100);
        ProductInventory product2 = createProduct(2L,"Product2", "description 2", 10, 100);
        productInventoryRepository.saveAll(List.of(product1, product2));

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order1 = createOrder(1L, product1, customer, LocalDateTime.now(), 5, 0.0);
        Order order2 = createOrder(1L, product2, customer, LocalDateTime.now(), 5, 0.0);
        orderRepository.saveAll(List.of(order1, order2));
        assertEquals(2, orderService.getAllOrders().size());
    }

    @Test
    void testGetOrderById_OrderFound() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 5, 0.0);
        orderRepository.save(order);
        Order foundOrder = orderService.getOrderById(1L);
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getProductInventory().getProductID());
        assertEquals(1L, foundOrder.getCustomer().getCustomerID());
        assertEquals(5, foundOrder.getOrderQuantity());
        assertEquals(0.0, foundOrder.getDiscount());
    }

    //negative order not found
    @Test
    void testGetOrderById_OrderNotFound() {
        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testAddOrder_Successful_InStock() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 200, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 5, 10.0);
        Order placeOrder = orderService.addOrderDetails(order);
        assertNotNull(placeOrder);
        assertEquals(1L, placeOrder.getProductInventory().getProductID());
        assertEquals(1L, placeOrder.getCustomer().getCustomerID());
        assertEquals(5, placeOrder.getOrderQuantity());
        assertEquals(10.0, placeOrder.getDiscount());
        assertEquals(1000, placeOrder.getTotalPrice());
        assertEquals(900, placeOrder.getDiscountedPrice());
    }

    //negative out of stock
    @Test
    void testAddOrder_Successful_OutOfStock() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 200, 5);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 10, 10.0);
        assertThrows(IllegalStateException.class, () -> orderService.addOrderDetails(order));
    }

    @Test
    void testDeleteOrder_Successful() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 200, 5);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 10, 10.0);
        orderRepository.save(order);
        orderService.deleteOrderDetails(1L);
        assertEquals(0, orderService.getAllOrders().size());
    }

    //negative order not found
    @Test
    void testDeleteOrder_OrderNotFound() {
        assertThatThrownBy(() -> orderService.deleteOrderDetails(1L))
                .isInstanceOf(NoSuchElementException.class);
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

    private Customer createCustomer(Long id, String name, String email, String contact, String address) {
        Customer customer = new Customer();
        customer.setCustomerID(id);
        customer.setFullName(name);
        customer.setEmailID(email);
        customer.setContactNumber(contact);
        customer.setAddress(address);
        return customer;
    }

    private Order createOrder(Long id, ProductInventory product, Customer customer, LocalDateTime orderTime, int quantity, double discount) {
        Order order = new Order();
        order.setOrderID(id);
        order.setProductInventory(product);
        order.setCustomer(customer);
        order.setOrderTimestamp(orderTime);
        order.setOrderQuantity(quantity);
        order.setDiscount(discount);
        return order;
    }
}
