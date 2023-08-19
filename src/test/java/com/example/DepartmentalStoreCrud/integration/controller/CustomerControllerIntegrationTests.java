package com.example.DepartmentalStoreCrud.integration.controller;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testGetAllCustomers() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerList.add(createCustomer(2L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerRepository.saveAll(customerList);
        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(customerRepository.findAll().size())));
    }

    @Test
    void testGetCustomerById_Successful() throws Exception {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName").value("Gurjot"))
                .andExpect(jsonPath("$.emailID").value("gurjot@gmail.com"))
                .andExpect(jsonPath("$.contactNumber").value("+919765412345"))
                .andExpect(jsonPath("$.address").value("123 Nangal"));
    }

    //negative case invalid customer id
    @Test
    void testGetCustomerById_CustomerNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerID}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrdersByCustomer_Successful() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerRepository.saveAll(customerList);
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerID}/orders", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //negative customer not found
    @Test
    void testGetOrdersByCustomer_CustomerNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerID}/orders", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddCustomer_Successful() throws Exception {
        String customerData = "{ \"customerID\": 1, \"fullName\": \"Gurjot\", \"address\": \"123 Nangal\", \"contactNumber\": \"+919765412345\", \"emailID\": \"gurjot@gmail.com\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerData))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("Customer added successfully"));
    }

    @Test
    void testUpdateCustomer_successful() throws Exception {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        String updateCustomer = "{ \"customerID\": 1, \"fullName\": \"Gurjot Singh\", \"address\": \"123 Nangal\", \"contactNumber\": \"+919765412345\", \"emailID\": \"gurjot@gmail.com\" }";

        mockMvc.perform(MockMvcRequestBuilders.put("/customers/{customerID}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCustomer))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer updated successfully with id: " + customer.getCustomerID()));
    }

    //negative customer not found
    @Test
    void testUpdateCustomer_CustomerNotFound() throws Exception {
        String updateCustomer = "{ \"customerID\": 1, \"fullName\": \"Gurjot\", \"address\": \"123 Nangal\", \"contactNumber\": \"+919765412345\", \"emailID\": \"gurjot@gmail.com\" }";
        mockMvc.perform(MockMvcRequestBuilders.put("/customers/{customerID}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCustomer))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomer_Successful() throws Exception {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{customerID}", 1L))
                .andExpect(status().isOk());
    }

    //negative customer not found
    @Test
    void testDeleteCustomer_CustomerNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{customerID}", 1L))
                .andExpect(status().isNotFound());
    }

    private Customer createCustomer(Long id, String name, String email, String contact, String address) {
        Customer customer = new Customer();
        customer.setCustomerID(id);
        customer.setAddress(address);
        customer.setContactNumber(contact);
        customer.setEmailID(email);
        customer.setFullName(name);
        return customer;
    }
}
