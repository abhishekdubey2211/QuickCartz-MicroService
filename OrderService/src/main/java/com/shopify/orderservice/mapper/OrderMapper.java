package com.shopify.orderservice.mapper;

import java.util.stream.Collectors;

import com.shopify.orderservice.dto.OrderDetailsDto;
import com.shopify.orderservice.dto.OrderDto;
import com.shopify.orderservice.model.Order;
import com.shopify.orderservice.model.OrderDetails;

public class OrderMapper {

    public static OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
                .orderId(order.getId())
                .orderDateTime(order.getDate())
                .totalAmount(order.getTotalamount())
                .orderDetails(order.getOrderdetails() != null ? 
                    order.getOrderdetails().stream()
                        .map(OrderMapper::toOrderDetailDto)
                        .collect(Collectors.toList()) 
                    : null)
                .paymentMode(order.getPaymentmode())
                .isPaymentDone(order.getIspaymentdone())
                .orderStatus(order.getStatus())
                .isdelevered(order.getIsdelevered())
                .build();
    }

    public static Order toOrderEntity(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }
        return Order.builder()
                .id(orderDto.getOrderId())
                .date(orderDto.getOrderDateTime())
                .totalamount(orderDto.getTotalAmount())
                .orderdetails(orderDto.getOrderDetails() != null ? 
                    orderDto.getOrderDetails().stream()
                        .map(OrderMapper::toOrderDetailEntity)
                        .collect(Collectors.toList()) 
                    : null)
                .paymentmode(orderDto.getPaymentMode())
                .ispaymentdone(orderDto.getIsPaymentDone())
                .status(orderDto.getOrderStatus())
                .isdelevered(orderDto.getIsdelevered())
                .build();
    }

    public static OrderDetailsDto toOrderDetailDto(OrderDetails orderDetails) {
        if (orderDetails == null) {
            return null;
        }
        return OrderDetailsDto.builder()
                .orderDetailId(orderDetails.getId())
                .productPrice(orderDetails.getPrice())
                .productId(orderDetails.getProduct())
                .quantity(orderDetails.getQuantity())
                .productName(orderDetails.getProductname())
                .productOrderStatus(orderDetails.getProductorderstatus())
                .build();
    }

    public static OrderDetails toOrderDetailEntity(OrderDetailsDto orderDetailsDto) {
        if (orderDetailsDto == null) {
            return null;
        }
        return OrderDetails.builder()
                .id(orderDetailsDto.getOrderDetailId())
                .price(orderDetailsDto.getProductPrice())
                .productname(orderDetailsDto.getProductName())
                .product(orderDetailsDto.getProductId())
                .quantity(orderDetailsDto.getQuantity())
                .productorderstatus(orderDetailsDto.getProductOrderStatus())
                .build();
    }
}
