package com.shopify.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<List<Order>> findByUser(long user);
}
