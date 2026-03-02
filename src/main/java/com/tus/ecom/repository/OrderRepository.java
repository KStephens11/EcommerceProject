package com.tus.ecom.repository;

import com.tus.ecom.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("""
        SELECT p.category, SUM(oi.quantity * oi.price)
        FROM OrderItemEntity oi
        JOIN oi.product p
        GROUP BY p.category
        ORDER BY SUM(oi.quantity * oi.price) DESC
    """)
    List<Object[]> findSalesByCategory();

}
