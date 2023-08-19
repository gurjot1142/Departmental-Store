package com.example.DepartmentalStoreCrud.bean;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "ProductInventory")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductInventory {

    /**
     * productID is the primary key for Product table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productID")
    private Long productID;

    @Column(name = "productDesc")
    private String productDesc;

    @Column(name = "productName")
    private String productName;

    @Column(name = "price")
    private double price;

    @Column(name = "productQuantity")
    private int productQuantity;

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "productID")
    private List<Order> orders = new ArrayList<>();
}
