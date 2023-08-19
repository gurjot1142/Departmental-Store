package com.example.DepartmentalStoreCrud.integration.repository;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
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
public class CustomerRepositoryTests {

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
        assertEquals(2, customerRepository.findAll().size());
    }

    @Test
    void testGetCustomerbyId() {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        Customer findCustomer = customerRepository.findById(1L).orElse(null);
        assertNotNull(findCustomer);
        assertEquals("Gurjot", customer.getFullName());
        assertEquals("gurjot@gmail.com", customer.getEmailID());
        assertEquals("+919765412345", customer.getContactNumber());
        assertEquals("123 Nangal", customer.getAddress());
    }

    @Test
    void testSaveCustomer() {
        Customer customer = createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal");
        customerRepository.save(customer);
        Customer foundCustomer = customerRepository.findById(1L).get();
        assertEquals(1, customerRepository.findAll().size());
        assertNotNull(foundCustomer);
        assertEquals("Gurjot", foundCustomer.getFullName());
        assertEquals("gurjot@gmail.com", foundCustomer.getEmailID());
        assertEquals("+919765412345", foundCustomer.getContactNumber());
        assertEquals("123 Nangal", foundCustomer.getAddress());
    }

    @Test
    void testDeleteByIDCustomer() {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(createCustomer(1L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerList.add(createCustomer(2L,"Gurjot", "gurjot@gmail.com",
                "+919765412345", "123 Nangal"));
        customerRepository.saveAll(customerList);
        customerRepository.deleteById(1L);
        assertEquals(1, customerRepository.findAll().size());
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
}
