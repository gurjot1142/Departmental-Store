package com.example.DepartmentalStoreCrud.integration.repository;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BackorderRepositoryTests {

    @Autowired
    private BackorderRepository backorderRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order sampleOrder;

    @BeforeEach
    void setup() {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 200, 5);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 10, 10.0);
        orderRepository.save(order);

        Backorder backorder = createBackorder(1L, order);
        backorderRepository.save(backorder);

        sampleOrder = order;
    }

    @Test
    void testGetAllBackorders() {
        assertNotNull(backorderRepository);
        assertEquals(1, backorderRepository.findAll().size());
    }

    @Test
    void testGetBackorderById() {
        Backorder findBackorder = backorderRepository.findById(1L).orElse(null);
        assertNotNull(findBackorder);
    }

    @Test
    void testSaveBackorder() {
        Backorder backorder = backorderRepository.findById(1L).get();
        assertNotNull(backorder);
    }

    @Test
    void testDeleteBackorderById() {
        backorderRepository.deleteById(1L);
        assertThat(backorderRepository.findAll().isEmpty());
    }

    @Test
    void testFindByOrder() {
        Backorder backorderByOrder = backorderRepository.findByOrder(sampleOrder);
        assertNotNull(backorderByOrder);
        assertEquals(10, backorderByOrder.getOrder().getOrderQuantity());
        assertEquals(10.0, backorderByOrder.getOrder().getDiscount());
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

    private Backorder createBackorder(Long id, Order order) {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(id);
        backorder.setOrder(order);
        return backorder;
    }
}
