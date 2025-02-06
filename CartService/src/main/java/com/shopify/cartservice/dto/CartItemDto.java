package com.shopify.cartservice.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopify.cartservice.model.Cart;
import com.shopify.cartservice.model.CartItem;

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
public class CartItemDto {
	private Long cartItemId;
	private double cartProductTotalPrice;
	private int productQuantity;
	private Long productId;

	public ErrorStatusDetails validateCartItemRequest(CartItemDto cartItemDTO) {

	    // Validate price
	    if (cartItemDTO.getCartProductTotalPrice() < 0) {
	        return new ErrorStatusDetails(8001, "Price must be zero or positive", HttpStatus.BAD_REQUEST);
	    }

	    // Validate quantity
	    if (cartItemDTO.getProductQuantity() <= 0) {
	        return new ErrorStatusDetails(8002, "Quantity must be at least 1", HttpStatus.BAD_REQUEST);
	    }

	    // Validate product ID
	    if (cartItemDTO.getProductId() == null || cartItemDTO.getProductId() <= 0) {
	        return new ErrorStatusDetails(6004, "Product ID must be a positive number", HttpStatus.BAD_REQUEST);
	    }

	    return null; // No errors
	}


}