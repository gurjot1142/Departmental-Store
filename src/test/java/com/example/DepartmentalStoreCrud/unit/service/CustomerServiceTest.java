package com.example.DepartmentalStoreCrud.unit.service;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer(1L)); // Sample customer with ID 1L
        customers.add(createCustomer(2L));
        when(customerRepository.findAll()).thenReturn(customers);
        assertNotNull(customers);
        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals(customers, result);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerById_ExistingCustomer() {
        // Arrange
        Long customerId = 1L;
        Customer customer = createCustomer(customerId); // Sample customer with ID 1L
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Argument captor
        ArgumentCaptor<Long> customerIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        Customer result = customerService.getCustomerById(customerId);

        // Assert
        assertEquals(customer, result);
        verify(customerRepository, times(1)).findById(customerIdCaptor.capture());
        assertEquals(customerId, customerIdCaptor.getValue());
    }


    @Test
    public void testGetCustomerById_NonExistingCustomer() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerService.getCustomerById(3L)).
                isInstanceOf(NoSuchElementException.class);
        verify(customerRepository, times(1)).findById(3L);
    }

    @Test
    public void testGetOrdersByCustomer() {
        // Arrange
        Long customerId = 1L;
        Customer customer = createCustomer(customerId);
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1L)); // Sample order with ID 1L and matching customer ID
        orders.add(createOrder(2L));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer_CustomerID(customerId)).thenReturn(orders);

        // Argument captor
        ArgumentCaptor<Long> customerIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        List<Order> result = customerService.getOrdersByCustomer(customerId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(orders, result);
        verify(customerRepository, times(1)).findById(customerIdCaptor.capture());
        assertEquals(customerId, customerIdCaptor.getValue());
        verify(orderRepository, times(1)).findByCustomer_CustomerID(customerIdCaptor.capture());
        assertEquals(customerId, customerIdCaptor.getValue());
    }

    @Test
    public void testAddCustomerDetails_ValidEmailContact() {
        Customer customer = createCustomer(1L); // Sample customer with ID 1L

        when(customerRepository.save(customer)).thenReturn(customer);

        // Argument captor
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        Customer newCustomer = customerService.addCustomerDetails(customer);
        assertNotNull(newCustomer);
        assertEquals(customer, newCustomer);
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        assertTrue(customer.getContactNumber().matches(customerService.getContactRegexp()));
        assertTrue(customer.getEmailID().matches(customerService.getEmailRegexp()));
        assertEquals(customer, customerCaptor.getValue());
    }

    @Test
    public void testAddCustomerDetails_NullInputValue() {
        // Arrange
        Customer customer = new Customer(); // Sample customer with ID 1L
        customer.setFullName(null);
        when(customerRepository.save(customer)).thenReturn(customer);

        assertThrows(IllegalArgumentException.class, () -> customerService.addCustomerDetails(customer));
        verify(customerRepository, never()).save(customer);
    }

    @Test
    public void testAddCustomerDetails_InvalidEmail() {
        // Arrange
        Customer customer = createCustomer(1L); // Sample customer with ID 1L
        customer.setEmailID("invalid-email");
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> customerService.addCustomerDetails(customer));
        verify(customerRepository, never()).save(customer);
    }

    @Test
    public void testAddCustomerDetails_InvalidContact() {
        // Arrange
        Customer customer = createCustomer(1L); // Sample customer with ID 1L
        customer.setContactNumber("1234567");
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act & Assert
        //assertThrows(IllegalArgumentException.class, () -> customerService.addCustomerDetails(customer));
        assertThatThrownBy(() -> customerService.addCustomerDetails(customer))
                .isInstanceOf(IllegalArgumentException.class);
        verify(customerRepository, never()).save(customer);
    }

    @Test
    public void testUpdateCustomerDetails_ValidEmailContact() {
        // Arrange
        Customer customer = createCustomer(1L); // Sample customer with ID 1L

        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        customer.setFullName("Gurjot");

        // Argument captor
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        Customer existingCustomer = customerService.updateCustomerDetails(customer.getCustomerID(), customer);
        assertNotNull(existingCustomer);
        assertEquals("Gurjot", customer.getFullName());
        assertTrue(customer.getContactNumber().matches(customerService.getContactRegexp()));
        assertTrue(customer.getEmailID().matches(customerService.getEmailRegexp()));
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        assertEquals(customer, customerCaptor.getValue());
    }

    @Test
    public void testUpdateCustomerDetails_InvalidEmail() {
        // Arrange
        Customer customer = createCustomer(1L); // Sample customer with ID 1L
        customer.setEmailID("invalid-email");

        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomerDetails(customer.getCustomerID(), customer));

        // Assert
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(customer);
    }

    @Test
    public void testUpdateCustomerDetails_InvalidContact() {
        // Arrange
        Customer customer = createCustomer(1L); // Sample customer with ID 1L
        customer.setContactNumber("1234567");

        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomerDetails(customer.getCustomerID(), customer));

        // Assert
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(customer);
    }

    @Test
    public void testDeleteCustomerDetails_ExistingCustomer() {
        // Arrange
        Long customerId = 1L;
        Customer customer = createCustomer(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ArgumentCaptor<Long> customerIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        customerService.deleteCustomerDetails(customerId);

        // Assert

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).deleteById(customerIdCaptor.capture());
        assertEquals(customerId, customerIdCaptor.getValue());
    }

    @Test
    public void testDeleteCustomerDetails_NonExistingCustomer() {
        // Arrange
        Long customerId = 3L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> customerService.deleteCustomerDetails(customerId));
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).delete(any());
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