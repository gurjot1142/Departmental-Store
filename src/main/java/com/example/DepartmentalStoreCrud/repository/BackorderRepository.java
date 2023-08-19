package com.example.DepartmentalStoreCrud.repository;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.bean.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackorderRepository extends JpaRepository<Backorder, Long> {
   Backorder findByOrder(Order order);
//   @Query("SELECT b FROM Backorder b WHERE b.order = :order")
//   Backorder findByOrder(@Param("order") Order order);
}
