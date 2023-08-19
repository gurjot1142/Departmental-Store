package com.example.DepartmentalStoreCrud.integration.repository;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private ProductInventory productInventory;

    @BeforeEach
    void setup() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 5, 0.0);
        orderRepository.save(order);

        productInventory = product;
    }

    @Test
    void testGetAllOrders() {
        assertNotNull(orderRepository);
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    void testGetOrderById() {
        Order foundOrder = orderRepository.findById(1L).get();
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getProductInventory().getProductID());
        assertEquals(1L, foundOrder.getCustomer().getCustomerID());
        assertEquals(5, foundOrder.getOrderQuantity());
        assertEquals(0.0, foundOrder.getDiscount());
    }

    @Test
    void testSaveOrder() {
        assertEquals(1, orderRepository.findAll().size());
        Order savedOrder = orderRepository.findById(1L).get();
        assertNotNull(savedOrder);
        assertEquals(1L, savedOrder.getProductInventory().getProductID());
        assertEquals(1L, savedOrder.getCustomer().getCustomerID());
        assertEquals(5, savedOrder.getOrderQuantity());
        assertEquals(0.0, savedOrder.getDiscount());
    }

    @Test
    void testDeleteOrderById() {
        orderRepository.deleteById(1L);
        assertEquals(0, orderRepository.findAll().size());
    }

    @Test
    void testFindOrdersByCustomer() {
        List<Order> orderList = orderRepository.findByCustomer_CustomerID(1L);
        assertNotNull(orderList);
        assertEquals(1, orderList.size());
    }

    @Test
    void testFindOrdersByProduct() {
        List<Order> orderList = orderRepository.findByProductInventory(productInventory);
        assertNotNull(orderList);
        assertEquals(1, orderList.size());
        assertEquals("Product 1", orderList.get(0).getProductInventory().getProductName());
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
