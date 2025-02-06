package com.shopify.userservice.dto;

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
public class CartDto {
    private long cartId;
    private double cartTotalAmount;
    private List<CartItemDto> cartItemList;
    
    
    public ErrorStatusDetails validateCartRequest(CartDto cartDto) {
        
        // Validate total amount
        if (cartDto.getCartTotalAmount() < 0) {
            return new ErrorStatusDetails(7002, "Total amount must be zero or positive", HttpStatus.BAD_REQUEST);
        }

       return null; // No errors
    }

}
