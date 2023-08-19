package com.example.DepartmentalStoreCrud.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Orders")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Order {

    /**
     * orderID is the primary key for Order table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderID")
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "productID")
    private ProductInventory productInventory;

    @ManyToOne
    @JoinColumn(name = "customerID")
    private Customer customer;

    @Column(name = "orderTimestamp")
    @CreationTimestamp
    private LocalDateTime orderTimestamp;

    @Column(name = "orderQuantity")
    private int orderQuantity;

    @Column(name = "discount")
    private double discount;

    @Column(name = "discountedPrice")
    private double discountedPrice;

    @Column(name = "totalPrice")
    private double totalPrice;
}
