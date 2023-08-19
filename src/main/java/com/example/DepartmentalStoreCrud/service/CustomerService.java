package com.example.DepartmentalStoreCrud.service;
import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerService {

    @Value("${email.regexp}")
    private String emailRegexp;

    @Value("${contact.regexp}")
    private String contactRegexp;

    /**
     * To get the email validation regex
     */
    public String getEmailRegexp() {
        return emailRegexp;
    }

    /**
     * To get the contact no validation regex
     */
    public String getContactRegexp() {
        return contactRegexp;
    }

    /**
     * Autowired CustomerRepository
     */
    @Autowired
    private CustomerRepository customRepo;

    /**
     * Autowired OrderRepository
     */
    @Autowired
    private OrderRepository orderRepo;

    /**
     * To get List of all Customers.
     *
     * @return List of Customers
     *
     */
    public List<Customer> getAllCustomers() {
        log.info("Customers fetched");
        return customRepo.findAll();
    }

    /**
     * To get Details of Customer with Customer's ID.
     *
     * @param customerID - Customer's ID
     * @return Customer Details
     */
    public Customer getCustomerById(final Long customerID) {
        Optional<Customer> customer = customRepo.findById(customerID);
        if (customer.isEmpty()) {
            log.info("Customer with given id not found");
            throw new NoSuchElementException("No customer exists with ID: " + customerID);
        }
        log.info("Customer found with id- " + customerID);
        return customer.get();
    }

    /**
     * To get orders placed by a customer
     *
     * @param customerID - Customer's details
     * @return Customer's Order details
     */
    public List<Order> getOrdersByCustomer(final Long customerID) {
        Optional<Customer> customer = customRepo.findById(customerID);
        if (customer.isEmpty()) {
            log.info("Customer with given id not found");
            throw new NoSuchElementException("No customer exists with ID: " + customerID);
        }
        log.info("Customer's orders fetched");
        return orderRepo.findByCustomer_CustomerID(customerID);
    }

    /**
     * To validate customer's contact no
     *
     * @param contact - Customer's contact no
     */
    private void validateContact(final String contact) {
        if (!contact.matches(contactRegexp)) {
            log.info("Invalid contact");
            throw new IllegalArgumentException("Invalid country code or contact number");
        }
    }

    /**
     * To validate customer's email
     *
     * @param email - Customer's email
     */
    private void validateEmail(final String email) {
        if (!email.matches(emailRegexp)) {
            log.info("Invalid email");
            throw new IllegalArgumentException("Invalid email");
        }
    }

    /**
     * To Add a new Customer.
     *
     * @param customer - Customer's details
     * @return Customer's Details
     */
    public Customer addCustomerDetails(final Customer customer) {
        if (StringUtils.hasText(customer.getFullName())
                && StringUtils.hasText(customer.getAddress())) {
            validateContact(customer.getContactNumber());
            validateEmail(customer.getEmailID());
        } else {
            log.info("Customer details missing");
            throw new IllegalArgumentException("Please enter valid customer details");
        }
        log.info("Customer added");
        return customRepo.save(customer);
    }

    /**
     * to Update Customer Details.
     *
     * @param customerID - Customer's ID
     * @param customer   - Customer's details
     * @return Customer's Details
     */
    public Customer updateCustomerDetails(final Long customerID, final Customer customer) {
        Optional<Customer> customerOptional = customRepo.findById(customerID);
        if (customerOptional.isEmpty()) {
            log.info("Invalid customer id");
            throw new NoSuchElementException("No customer exists with ID: " + customerID);
        }
        Customer existingCustomer = customerOptional.get();
        //setting the values and checking for null
        existingCustomer.setFullName(customer.getFullName() == null ? existingCustomer.getFullName() : customer.getFullName());
        existingCustomer.setAddress(customer.getAddress() == null ? existingCustomer.getAddress() : customer.getAddress());
        existingCustomer.setContactNumber(customer.getContactNumber() == null ? existingCustomer.getContactNumber() : customer.getContactNumber());
        existingCustomer.setEmailID(customer.getEmailID() == null ? existingCustomer.getEmailID() : customer.getEmailID());
        //validation
        validateContact(existingCustomer.getContactNumber());
        validateEmail(existingCustomer.getEmailID());
        log.info("Customer updated with id-" + customerID);
        return customRepo.save(existingCustomer);
    }

    /**
     * to Delete a Customer.
     *
     * @param customerID - Customer's ID
     */
    public void deleteCustomerDetails(final Long customerID) {
        Optional<Customer> customer = customRepo.findById(customerID);
        if (customer.isEmpty()) {
            log.info("Invalid customer id");
            throw new NoSuchElementException("No customer exists with ID: " + customerID);
        }
        log.info("Customer deleted with id-" + customerID);
        customRepo.deleteById(customerID);
    }
}
