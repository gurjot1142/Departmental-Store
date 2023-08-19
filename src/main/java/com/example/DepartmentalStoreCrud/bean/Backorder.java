package com.example.DepartmentalStoreCrud.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

@Entity
@Data
@Table(name = "Backorder")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Backorder {

    /**
     * backorderID is the primary key for Backorder table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backorderID;

    @OneToOne
    @JoinColumn(name = "orderID", referencedColumnName = "orderID")
    private Order order;
}
