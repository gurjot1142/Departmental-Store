package com.example.DepartmentalStoreCrud.unit.controller;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.controller.CustomerController;
import com.example.DepartmentalStoreCrud.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = AutoConfigureMockMvc.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void getAllCustomersTest() throws Exception {
        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer(1L));
        customers.add(createCustomer(2L));
        when(customerService.getAllCustomers()).thenReturn(customers);
        this.mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(customers.size())));
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerByIDTest() throws Exception {
        Customer customer = createCustomer(1L);
        when(customerService.getCustomerById(anyLong())).thenReturn(customer);
        this.mockMvc.perform(get("/customers/{customerID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName", is(customer.getFullName())))
                .andExpect(jsonPath("$.address", is(customer.getAddress())))
                .andExpect(jsonPath("$.contactNumber", is(customer.getContactNumber())))
                .andExpect(jsonPath("$.emailID", is(customer.getEmailID())));
        verify(customerService, times(1)).getCustomerById(1L);
    }

    //negative case
    @Test
    void getCustomerByIDTest_CustomerNotFound() throws Exception {
        Long invalidCustomerId = 100L;
        when(customerService.getCustomerById(invalidCustomerId)).thenReturn(null);
        mockMvc.perform(get("/{customerID}", invalidCustomerId))
                .andExpect(status().isNotFound());

        verify(customerService, never()).getCustomerById(invalidCustomerId);
    }

    @Test
    void getOrdersByCustomerTest() throws Exception {
        Long customerId = 1L;
        Customer customer = createCustomer(customerId);
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1L));
        orders.add(createOrder(2L));
        when(customerService.getOrdersByCustomer(anyLong())).thenReturn(orders);

        this.mockMvc.perform(get("/customers/{customerID}/orders", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderID").exists())
                .andExpect(jsonPath("$[0].orderTimestamp").exists())
                .andExpect(jsonPath("$[0].orderQuantity").exists())
                .andExpect(jsonPath("$[0].discount").exists())
                .andExpect(jsonPath("$[0].discountedPrice").exists())
                .andExpect(jsonPath("$[0].customer.customerID").exists())
                .andExpect(jsonPath("$[0].customer.fullName").exists())
                .andExpect(jsonPath("$[0].productInventory.productID").exists())
                .andExpect(jsonPath("$[0].productInventory.productName").exists())
                .andExpect(jsonPath("$[0].productInventory.productQuantity").exists())
                .andExpect(jsonPath("$[0].productInventory.price").exists())
                .andExpect(jsonPath("$[1].orderID").exists())
                .andExpect(jsonPath("$[1].orderTimestamp").exists())
                .andExpect(jsonPath("$[1].orderQuantity").exists())
                .andExpect(jsonPath("$[1].discount").exists())
                .andExpect(jsonPath("$[1].discountedPrice").exists())
                .andExpect(jsonPath("$[1].customer.customerID").exists())
                .andExpect(jsonPath("$[1].customer.fullName").exists())
                .andExpect(jsonPath("$[1].productInventory.productID").exists())
                .andExpect(jsonPath("$[1].productInventory.productName").exists())
                .andExpect(jsonPath("$[1].productInventory.productQuantity").exists())
                .andExpect(jsonPath("$[1].productInventory.price").exists());

        verify(customerService, times(1)).getOrdersByCustomer(customerId);
    }

    @Test
    void addCustomer() throws Exception {
        Customer customer = createCustomer(1L);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        when(customerService.addCustomerDetails(customerCaptor.capture())).thenReturn(customer);
        this.mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Customer added successfully"));

        assertThat(customerCaptor.getValue().getFullName(), is("John Doe"));
        assertThat(customerCaptor.getValue().getAddress(), is("123 Main Street"));
        assertThat(customerCaptor.getValue().getContactNumber(), is("+919417665710"));
        assertThat(customerCaptor.getValue().getEmailID(), is("johndoe@gmail.com"));
    }

    @Test
    void updateCustomer() throws Exception {
        Customer customer = createCustomer(1L);
        when(customerService.updateCustomerDetails(anyLong(), any(Customer.class))).thenReturn(customer);
        this.mockMvc.perform(put("/customers/{customerID}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer updated successfully with id: " + customer.getCustomerID()));
        verify(customerService, times(1)).updateCustomerDetails(customer.getCustomerID(), customer);
    }

    //negative case
    @Test
    void updateCustomerTest_NonExistentCustomer() throws Exception {
        Long nonExistentCustomerId = 999L;

        Customer customer = createCustomer(nonExistentCustomerId);

        when(customerService.updateCustomerDetails(nonExistentCustomerId, customer)).thenReturn(null);

        mockMvc.perform(put("/{customerID}", nonExistentCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());

        verify(customerService, never()).updateCustomerDetails(nonExistentCustomerId, customer);
    }

    @Test
    void deleteCustomerTest() throws Exception {
        doNothing().when(customerService).deleteCustomerDetails(anyLong());
        this.mockMvc.perform(delete("/customers/{customerID}", 2L))
                .andExpect(status().isOk());
         verify(customerService, times(1)).deleteCustomerDetails(2L);
    }

    //negative case
    @Test
    void deleteCustomerTest_CustomerNotFound() throws Exception {
        Long invalidCustomerId = 999L;
        doNothing().when(customerService).deleteCustomerDetails(invalidCustomerId);

        mockMvc.perform(delete("/{customerID}", invalidCustomerId))
                .andExpect(status().isNotFound());

        verify(customerService, never()).deleteCustomerDetails(invalidCustomerId);
    }

    private Customer createCustomer(Long customerId) {
        Customer customer = new Customer();
        customer.setCustomerID(customerId);
        customer.setFullName("John Doe");
        customer.setAddress("123 Main Street");
        customer.setContactNumber("+919417665710");
        customer.setEmailID("johndoe@gmail.com");
        return customer;
    }

    private Order createOrder(Long orderId) {
        Order order = new Order();
        order.setOrderID(orderId);
        order.setOrderTimestamp(LocalDateTime.now());
        order.setOrderQuantity(2);
        order.setDiscount(10.0);
        order.setDiscountedPrice(90.0);

        Customer customer = new Customer();
        customer.setCustomerID(1L);
        customer.setFullName("John Doe");
        order.setCustomer(customer);

        ProductInventory productInventory = new ProductInventory();
        productInventory.setProductID(1L);
        productInventory.setProductName("Product 1");
        productInventory.setProductQuantity(5);
        productInventory.setPrice(100.0);
        order.setProductInventory(productInventory);
        return order;
    }
}
