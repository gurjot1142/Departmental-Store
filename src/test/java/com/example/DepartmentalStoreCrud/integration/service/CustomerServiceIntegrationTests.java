package com.example.DepartmentalStoreCrud.integration.service;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.service.CustomerService;
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
public class CustomerServiceIntegrationTests {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testGetAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                        "+919765412345", "123 Nangal"));
        customerList.add(createCustomer(2L,"Gurjot", "gurjot@gmail.com",
                        "+919765412345", "123 Nangal"));
        customerRepository.saveAll(customerList);
        assertEquals(2,customerService.getAllCustomers().size());
    }

    @Test
    void testGetCustomerById_CustomerFound() {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        Customer foundCustomer = customerService.getCustomerById(1L);
        assertNotNull(foundCustomer);
        assertEquals("Gurjot", foundCustomer.getFullName());
        assertEquals("gurjot@gmail.com", foundCustomer.getEmailID());
        assertEquals("+919765412345", foundCustomer.getContactNumber());
        assertEquals("123 Nangal", foundCustomer.getAddress());
    }

    //negative case invalid customer id
    @Test
    void testGetCustomerById_CustomerNotFound() {
        assertThatThrownBy(() -> customerService.getCustomerById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testAddCustomer_Successful() {
         Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                 "+919765412345", "123 Nangal");
         Customer newCustomer = customerService.addCustomerDetails(customer);
         assertNotNull(newCustomer);
         assertEquals("Gurjot", newCustomer.getFullName());
         assertEquals("gurjot@gmail.com", newCustomer.getEmailID());
         assertEquals("+919765412345", newCustomer.getContactNumber());
         assertEquals("123 Nangal", newCustomer.getAddress());
    }

    //negative case (email invalid)
    @Test
    void testAddCustomer_invalidData() {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot.com",
                "+919765412345", "123 Nangal");
        assertThatThrownBy(() -> customerService.addCustomerDetails(customer))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateCustomerDetails_Successful() {
        customerRepository.save(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        Customer updatedCustomer = customerService.updateCustomerDetails(1L,
                createCustomer(1L,"Gurjot Singh", "gurjot@gmail.com",
                        "+919765412345", "123 Nangal"));
        assertEquals("Gurjot Singh", updatedCustomer.getFullName());
    }

    //negative customer not found
    @Test
    void testUpdateCustomerDetails_CustomerNotFound() {
        Customer customer = new Customer();
        assertThatThrownBy(() -> customerService.updateCustomerDetails(2L, customer))
                .isInstanceOf(NoSuchElementException.class);
    }

    //negative updated data invalid
    @Test
    void testUpdateCustomerDetails_UpdateDataInvalid() {
        customerRepository.save(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        Customer updatedCustomer = createCustomer(1L,"Gurjot", "gurjot.com",
                        "+919765412345", "123 Nangal");  //invalid email
        assertThatThrownBy(() -> customerService.updateCustomerDetails(1L, updatedCustomer))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDeleteCustomerDetails_Successful() {
        customerRepository.save(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerService.deleteCustomerDetails(1L);
        assertEquals(0, customerService.getAllCustomers().size());
    }

    //negative customer not found
    @Test
    void testDeleteCustomerDetails_CustomerNotFound() {
        assertThatThrownBy(() -> customerService.deleteCustomerDetails(1L))
                .isInstanceOf(NoSuchElementException.class);
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
