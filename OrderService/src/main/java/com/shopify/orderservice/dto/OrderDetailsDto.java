package com.shopify.orderservice.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
public class OrderDetailsDto {
	private Long orderDetailId;
	private double productPrice;
	private Long productId;
	private String productName;
	private int quantity;
	private String productOrderStatus;

	public static ErrorStatusDetails validateOrderDetailsRequest(OrderDetailsDto orderDetailsDto) {
//		if (orderDetailsDto.getProductPrice() < 0) {
//			return new ErrorStatusDetails(7008, "Product price must be zero or positive", HttpStatus.BAD_REQUEST);
//		}
		if (orderDetailsDto.getProductId() == null || orderDetailsDto.getProductId() <= 0) {
			return new ErrorStatusDetails(7009, "Product ID must be a positive number", HttpStatus.BAD_REQUEST);
		}
		if (orderDetailsDto.getQuantity() <= 0) {
			return new ErrorStatusDetails(7010, "Quantity must be a positive number", HttpStatus.BAD_REQUEST);
		}
//		if (orderDetailsDto.getProductOrderStatus() == null
//				|| orderDetailsDto.getProductOrderStatus().trim().isEmpty()) {
//			return new ErrorStatusDetails(7011, "Product order status is mandatory", HttpStatus.BAD_REQUEST);
//		}
		return null;
	}
}
