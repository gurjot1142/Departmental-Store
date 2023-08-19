package com.example.DepartmentalStoreCrud.repository;

import com.example.DepartmentalStoreCrud.bean.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

}
