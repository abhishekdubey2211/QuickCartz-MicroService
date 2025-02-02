package com.shopify.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.orderservice.model.OrderDetails;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long>{

}
