package com.shopify.orderservice.service;

import java.util.List;

import com.shopify.orderservice.dto.OrderDto;

public interface OrderService {
	OrderDto placeOrder(Long userid, OrderDto pushOrderDto);

	OrderDto getPlacedOrderDetailsByOrderId(Long orderid);

	List<OrderDto> getOrdersByUserId(Long userid);

	OrderDto cancelOrder(Long orderId);

	OrderDto cancelOrderItem(Long orderId, Long productId);
}
