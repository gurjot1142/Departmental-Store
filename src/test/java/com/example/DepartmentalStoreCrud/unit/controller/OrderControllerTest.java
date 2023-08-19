package com.example.DepartmentalStoreCrud.unit.controller;

import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.controller.OrderController;
import com.example.DepartmentalStoreCrud.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ContextConfiguration(classes = AutoConfigureMockMvc.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void getAllOrdersTest() throws Exception {
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1L));
        orders.add(createOrder(2L));

        when(orderService.getAllOrders()).thenReturn(orders);

        this.mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(orders.size())));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    public void getOrderByIdTest() throws Exception {
        Order order = createOrder(1L);
        when(orderService.getOrderById(anyLong())).thenReturn(order);
        this.mockMvc.perform(get("/orders/{orderID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(orderService, times(1)).getOrderById(1L);
    }

    //negative case
    @Test
    void getOrderByIdTest_OrderNotFound() throws Exception {
        Long nonExistentOrderId = 999L;

        when(orderService.getOrderById(nonExistentOrderId)).thenReturn(null);

        mockMvc.perform(get("/{orderID}", nonExistentOrderId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addOrderDetailsTest() throws Exception {
        Order order = createOrder(1L);

        this.mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order placed successfully."));

        verify(orderService, times(1)).addOrderDetails(order);
    }

    @Test
    public void updateOrderDetailsTest() throws Exception {
        Long orderId = 1L;
        Order order = createOrder(orderId);

        this.mockMvc.perform(put("/orders/{orderID}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order updated successfully."));

        verify(orderService, times(1)).updateOrderDetails(eq(orderId), any(Order.class));
    }

    //negative case
    @Test
    void updateOrderDetailsTest_NonExistentOrder() throws Exception {
        Long nonExistentOrderId = 999L;
        Order order = createOrder(nonExistentOrderId);

        when(orderService.updateOrderDetails(nonExistentOrderId, order)).thenReturn(null);

        mockMvc.perform(put("/{orderID}", nonExistentOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isNotFound());

        verify(orderService, never()).updateOrderDetails(nonExistentOrderId, order);
    }

    @Test
    public void deleteOrderDetailsTest() throws Exception {
        Long orderId = 1L;

        this.mockMvc.perform(delete("/orders/{orderID}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully."));

        verify(orderService, times(1)).deleteOrderDetails(orderId);
    }

    //negative case
    @Test
    void deleteOrderDetailsTest_NonExistentOrder() throws Exception {
        Long nonExistentOrderId = 999L;

        doNothing().when(orderService).deleteOrderDetails(nonExistentOrderId);

        mockMvc.perform(delete("/{orderID}", nonExistentOrderId))
                .andExpect(status().isNotFound());

        verify(orderService, never()).deleteOrderDetails(nonExistentOrderId);
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
}
