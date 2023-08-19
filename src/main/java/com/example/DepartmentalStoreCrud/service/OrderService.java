package com.example.DepartmentalStoreCrud.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.bean.Customer;
import com.example.DepartmentalStoreCrud.bean.Order;
import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.repository.CustomerRepository;
import com.example.DepartmentalStoreCrud.repository.OrderRepository;
import com.example.DepartmentalStoreCrud.repository.ProductInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    /**
     * Autowired OrderRepository
     */
    @Autowired
    private OrderRepository orderRepo;

    /**
     * Autowired BackorderService
     */
    @Autowired
    private BackorderService backorderService;

    /**
     * Autowired ProductInventoryRepository
     */
    @Autowired
    private ProductInventoryRepository productInventoryRepo;

    /**
     * Autowired CustomerRepository
     */
    @Autowired
    private CustomerRepository customerRepo;

    /**
     * Autowired BackorderRepository
     */
    @Autowired
    private BackorderRepository backorderRepo;

    /**
     * To get List of all orders placed by customers.
     *
     * @return List of Orders
     *
     */
    public List<Order> getAllOrders() {
        log.info("Orders fetched");
        return orderRepo.findAll();
    }

    /**
     * To get Details of Order with Order's ID.
     *
     * @param orderID - order's ID
     * @return Order Details
     */
    public Order getOrderById(final Long orderID) {
        Optional<Order> order = orderRepo.findById(orderID);
        if (order.isEmpty()) {
            log.info("Invalid order id");
            throw new NoSuchElementException("No order exists with ID: " + orderID);
        }
        log.info("Order found with id-" + orderID);
        return order.get();
    }

    /**
     * To get Details of customer and product for the order to be placed.
     *
     * @param order - Order Details
     */
    private void fetchOtherEntities(final Order order) {
        Long customerID = order.getCustomer().getCustomerID();
        Long productID = order.getProductInventory().getProductID();
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new NoSuchElementException("No customer exists with ID: " + order.getCustomer().getCustomerID()));
        ProductInventory productInventory = productInventoryRepo.findById(productID)
                .orElseThrow(() -> new NoSuchElementException("No product exists with ID: " + order.getProductInventory().getProductID()));
        order.setCustomer(customer);
        order.setProductInventory(productInventory);
        log.info("Other entities fetched");
        orderRepo.save(order);
    }

    /**
     * To apply discount on the placed order.
     *
     * @param order - Order Details
     */
    private void applyDiscount(final Order order) {
        ProductInventory productInventory = order.getProductInventory();
        double totalPrice = order.getOrderQuantity() * productInventory.getPrice();
        order.setTotalPrice(totalPrice);
        double discountedPrice = order.getTotalPrice() - order.getTotalPrice() * (order.getDiscount() / 100.0);
        order.setDiscountedPrice(discountedPrice);
    }

    /**
     * To check if the order is to be placed as a backorder or not
     *
     * @param order - Order Details
     */
    private void checkIfBackorder(final Order order) {
        ProductInventory productInventory = order.getProductInventory();
        if (productInventory.getProductQuantity() >= order.getOrderQuantity()) {
            productInventory.setProductQuantity(productInventory.getProductQuantity() - order.getOrderQuantity());
            productInventoryRepo.save(productInventory);
            log.info("Order placed successfully");
        } else {
            Order savedOrder = orderRepo.save(order);
            // Create a backorder for the order
            Backorder backorder = new Backorder();
            backorder.setOrder(savedOrder);
            backorderService.createBackorder(backorder);
            log.info("Given order is placed as a backorder");
            throw new IllegalStateException("Order placed successfully as a backorder");
        }
    }

    /**
     * To add/place an order
     *
     * @param order - Order Details
     * @return - Order Details
     */
    public Order addOrderDetails(final Order order) {
        fetchOtherEntities(order);
        applyDiscount(order);
        checkIfBackorder(order);
        return orderRepo.save(order);
    }

    /**
     * To update an order
     *
     * @param order - Order Details
     * @return - Order details
     */
    public Order updateOrderDetails(final Long orderID, final Order order) {
        fetchOtherEntities(order);
        checkIfBackorder(order);
        log.info("Order updated successfully");
        return orderRepo.save(order);
    }

    /**
     * To delete/cancel an order
     *
     * @param orderID - Order's ID
     */
    public void deleteOrderDetails(final Long orderID) {
        Order savedOrder = getOrderById(orderID);
        ProductInventory productInventory = savedOrder.getProductInventory();

        Backorder backorder = backorderRepo.findByOrder(savedOrder);
        if (backorder != null) {
            backorderService.deleteBackorder(backorder.getBackorderID());
        } else {
            productInventory.setProductQuantity(productInventory.getProductQuantity() + savedOrder.getOrderQuantity());
        }
        log.info("Order deleted successfully with id-" + orderID);
        orderRepo.deleteById(orderID);
    }
}
