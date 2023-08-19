package com.example.DepartmentalStoreCrud.unit.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import com.example.DepartmentalStoreCrud.service.BackorderService;
import com.example.DepartmentalStoreCrud.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BackorderService backorderService;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BackorderRepository backorderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1L)); // Sample order with ID 1L
        orders.add(createOrder(2L));
        when(orderRepository.findAll()).thenReturn(orders);
        assertNotNull(orders);
        List<Order> result = orderService.getAllOrders();
        assertEquals(2, result.size());
        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById_ExistingOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = createOrder(orderId); // Sample order with ID 1L
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //Argument Captor
        ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        Order result = orderService.getOrderById(orderId);

        // Assert
        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(orderIdCaptor.capture());
        assertEquals(orderId, orderIdCaptor.getValue());
    }

    @Test
    public void testGetOrderById_NonExistingOrder() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(NoSuchElementException.class);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void testAddOrderDetails_Successful() {
        // Arrange
        Order order = createOrder(1L); // Sample order with ID 1L
        when(orderRepository.save(order)).thenReturn(order);
        when(productInventoryRepository.findById(order.getProductInventory().getProductID())).thenReturn(Optional.of(order.getProductInventory()));
        when(customerRepository.findById(order.getCustomer().getCustomerID())).thenReturn(Optional.of(order.getCustomer()));

        // Argument captor
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        Order newOrder = orderService.addOrderDetails(order);
        assertNotNull(newOrder);
        assertEquals(order, newOrder);
        assertEquals(200, newOrder.getTotalPrice());
        assertEquals(180, newOrder.getDiscountedPrice());
        verify(orderRepository, times(2)).save(orderCaptor.capture());
        verify(productInventoryRepository, times(1)).findById(order.getProductInventory().getProductID());
        verify(customerRepository, times(1)).findById(order.getCustomer().getCustomerID());
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    public void testAddOrderDetails_OutOfStock() {
        // Arrange
        Order order = createOrder(1L); // Sample order with ID 1L
        Backorder backorder = Mockito.mock(Backorder.class);
        backorderRepository.save(backorder);
        ProductInventory productInventory = order.getProductInventory();
        productInventory.setProductQuantity(0);
        when(productInventoryRepository.findById(productInventory.getProductID())).thenReturn(Optional.of(productInventory));
        when(customerRepository.findById(order.getCustomer().getCustomerID())).thenReturn(Optional.of(order.getCustomer()));
        backorderService.createBackorder(backorder);
        when(backorderRepository.save(backorder)).thenReturn(backorder);
        assertThrows(IllegalStateException.class, () -> orderService.addOrderDetails(order));
        verify(orderRepository, times(2)).save(order);
        verify(productInventoryRepository, times(1)).findById(productInventory.getProductID());
        verify(customerRepository, times(1)).findById(order.getCustomer().getCustomerID());
    }

    @Test
    public void testUpdateOrderDetails_Successful() {
        // Arrange
        Order order = createOrder(1L); // Sample order with ID 1L
        when(productInventoryRepository.findById(order.getProductInventory().getProductID())).thenReturn(Optional.of(order.getProductInventory()));
        when(customerRepository.findById(order.getCustomer().getCustomerID())).thenReturn(Optional.of(order.getCustomer()));
        when(orderRepository.save(order)).thenReturn(order);
        order.setOrderQuantity(3);

        // Argument captor
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        Order newOrder = orderService.updateOrderDetails(order.getOrderID(), order);
        assertNotNull(newOrder);
        assertEquals(3, order.getOrderQuantity());
        verify(orderRepository, times(2)).save(orderCaptor.capture());
        verify(productInventoryRepository, times(1)).findById(order.getProductInventory().getProductID());
        verify(customerRepository, times(1)).findById(order.getCustomer().getCustomerID());
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    public void testUpdateOrderDetails_OutOfStock() {
        // Arrange
        Order order = createOrder(1L); // Sample order with ID 1L
        Backorder backorder = createBackorder(order);
        ProductInventory productInventory = order.getProductInventory();
        productInventory.setProductQuantity(0);
        when(productInventoryRepository.findById(productInventory.getProductID())).thenReturn(Optional.of(productInventory));
        when(customerRepository.findById(order.getCustomer().getCustomerID())).thenReturn(Optional.of(order.getCustomer()));
        when(backorderRepository.save(backorder)).thenReturn(backorder);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.updateOrderDetails(order.getOrderID(), order));
        verify(orderRepository, times(2)).save(order);
        verify(productInventoryRepository, times(1)).findById(productInventory.getProductID());
        verify(customerRepository, times(1)).findById(order.getCustomer().getCustomerID());
    }

    @Test
    public void testDeleteOrderDetails_OrderWithBackorder() {
        // Arrange
        Long orderId = 1L;
        Order order = createOrder(orderId); // Sample order with ID 1L
        Backorder backorder = createBackorder(order);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(backorderRepository.findByOrder(order)).thenReturn(backorder);

        // Assert
        verify(backorderRepository, never()).deleteById(anyLong());
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteOrderDetails_OrderWithoutBackorder() {
        // Arrange
        Long orderId = 1L;
        Order order = createOrder(orderId); // Sample order with ID 1L
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(backorderRepository.findByOrder(order)).thenReturn(null);

        //Argument captor
        ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        orderService.deleteOrderDetails(orderId);

        // Assert
        verify(backorderRepository, never()).deleteById(anyLong());
        verify(orderRepository, times(1)).deleteById(orderIdCaptor.capture());
        assertEquals(orderId, orderIdCaptor.getValue());
    }

    @Test
    public void testDeleteOrderDetails_NonExistingOrder() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> orderService.deleteOrderDetails(orderId));
        verify(backorderRepository, never()).deleteById(anyLong());
        verify(orderRepository, never()).deleteById(anyLong());
    }


    private Order createOrder(Long orderId) {
        Order order = new Order();
        order.setOrderID(orderId);
        order.setOrderTimestamp(LocalDateTime.now());
        order.setOrderQuantity(2);
        order.setDiscount(10.0);

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

    private Backorder createBackorder(Order order) {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(backorder.getBackorderID());
        backorder.setOrder(order);
        return backorder;
    }
}