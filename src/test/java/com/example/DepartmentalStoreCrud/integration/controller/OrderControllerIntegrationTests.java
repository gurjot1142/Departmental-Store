package com.example.DepartmentalStoreCrud.integration.controller;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testGetAllOrders() throws Exception {
        ProductInventory product1 = createProduct(1L, "Product 1", "Description 1", 10, 100);
        ProductInventory product2 = createProduct(2L,"Product2", "description 2", 10, 100);
        productInventoryRepository.saveAll(List.of(product1, product2));

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        List<Order> orderList = new ArrayList<>();
        Order order1 = createOrder(1L, product1, customer, LocalDateTime.now(), 5, 0.0);
        Order order2 = createOrder(2L, product2, customer, LocalDateTime.now(), 5, 0.0);
        orderList.add(order1);
        orderList.add(order2);
        orderRepository.saveAll(orderList);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(orderList.size())));
    }

    @Test
    void testGetOrderById_OrderFound() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 5, 0.0);
        orderRepository.save(order);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productInventory.productID").value(product.getProductID()))
                .andExpect(jsonPath("$.customer.customerID").value(customer.getCustomerID()))
                .andExpect(jsonPath("$.orderQuantity").value(5))
                .andExpect(jsonPath("$.discount").value(0.0));
    }

    //negative order not found
    @Test
    void testGetOrderById_OrderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{orderID}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddOrder_Successful_InStock() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 100, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        String orderData = "{ \"productInventory\": { \"productID\": 1 }, \"customer\": { \"customerID\": 1 }, \"orderQuantity\": 5, \"discount\": 10 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderData))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Order placed successfully."));
    }

    @Test
    void testAddOrder_OutOStock() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 100, 5);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "abc@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        String orderData = "{ \"productInventory\": { \"productID\": 1 }, \"customer\": { \"customerID\": 1 }, \"orderQuantity\": 10, \"discount\": 20 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderData))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteOrder_Successful() throws Exception {
        ProductInventory product = createProduct(1L, "Product 1", "Description 1", 10, 100);
        productInventoryRepository.save(product);

        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com", "+919765412345", "123 Nangal");
        customerRepository.save(customer);

        Order order = createOrder(1L, product, customer, LocalDateTime.now(), 5, 0.0);
        orderRepository.save(order);
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/{orderID}", 1L))
                .andExpect(status().isOk());
    }

    //negative order not found
    @Test
    void testDeleteOrder_OrderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/{orderID}", 1L))
                .andExpect(status().isNotFound());
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
