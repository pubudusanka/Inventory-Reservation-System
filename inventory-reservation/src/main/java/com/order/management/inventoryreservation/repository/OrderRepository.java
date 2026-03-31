package com.order.management.inventoryreservation.repository;

import com.order.management.inventoryreservation.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
