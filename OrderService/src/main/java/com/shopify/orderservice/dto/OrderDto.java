package com.shopify.orderservice.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderDto {
	private long orderId;
	private String orderDateTime;
	private double totalAmount;
	private List<OrderDetailsDto> orderDetails;
	private String paymentMode;
	private int isPaymentDone;
	private String orderStatus;
	private String isdelevered;

	public static ErrorStatusDetails validateOrderRequest(OrderDto orderDto) {
//		if (orderDto.getOrderDateTime() == null || orderDto.getOrderDateTime().trim().isEmpty()) {
//			return new ErrorStatusDetails(7001, "Order date is mandatory", HttpStatus.BAD_REQUEST);
//		}
//		if (orderDto.getTotalAmount() < 0) {
//			return new ErrorStatusDetails(7002, "Total amount must be zero or positive", HttpStatus.BAD_REQUEST);
//		}
		if (orderDto.getOrderDetails() == null || orderDto.getOrderDetails().isEmpty()) {
			return new ErrorStatusDetails(7003, "Order details cannot be empty", HttpStatus.BAD_REQUEST);
		}
		if (orderDto.getPaymentMode() == null || orderDto.getPaymentMode().trim().isEmpty()) {
			return new ErrorStatusDetails(7004, "Payment mode is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (orderDto.getIsPaymentDone() < 0 || orderDto.getIsPaymentDone() > 1) {
			return new ErrorStatusDetails(7005, "isPaymentDone must be 0 (false) or 1 (true)", HttpStatus.BAD_REQUEST);
		}
//		if (orderDto.getOrderStatus() == null || orderDto.getOrderStatus().trim().isEmpty()) {
//			return new ErrorStatusDetails(7006, "Order status is mandatory", HttpStatus.BAD_REQUEST);
//		}
//		if (orderDto.getIsdelevered() == null
//				|| (!orderDto.getIsdelevered().equals("YES") && !orderDto.getIsdelevered().equals("NO"))) {
//			return new ErrorStatusDetails(7007, "isDelivered must be either 'YES' or 'NO'", HttpStatus.BAD_REQUEST);
//		}
		return null;
	}
}
