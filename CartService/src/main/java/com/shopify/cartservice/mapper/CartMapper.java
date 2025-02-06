package com.shopify.cartservice.mapper;

import com.shopify.cartservice.dto.CartDto;
import com.shopify.cartservice.dto.CartItemDto;
import com.shopify.cartservice.model.Cart;
import com.shopify.cartservice.model.CartItem;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public static CartDto toCartDto(Cart cart) {
        return CartDto.builder()
                .cartId(cart.getId())
                .cartTotalAmount(cart.getTotalamount())
                .cartItemList(cart.getCartitem().stream()
                        .map(CartMapper::toCartItemDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Cart toCart(CartDto cartDto) {
        Cart cart = Cart.builder()
                .id(cartDto.getCartId())
                .totalamount(cartDto.getCartTotalAmount())
                .cartitem(cartDto.getCartItemList().stream()
                        .map(CartMapper::toCartItem)
                        .collect(Collectors.toList()))
                .build();

        // Set the cart reference in cart items
        cart.getCartitem().forEach(cartItem -> cartItem.setCart(cart));
        return cart;
    }

    public static CartItemDto toCartItemDto(CartItem cartItem) {
        return CartItemDto.builder()
                .cartItemId(cartItem.getId())
                .cartProductTotalPrice(cartItem.getPrice())
                .productQuantity(cartItem.getQuantity())
                .productId(cartItem.getProductid())
                .build();
    }

    public static CartItem toCartItem(CartItemDto cartItemDto) {
        return CartItem.builder()
                .id(cartItemDto.getCartItemId())
                .price(cartItemDto.getCartProductTotalPrice())
                .quantity(cartItemDto.getProductQuantity())
                .productid(cartItemDto.getProductId())
                .build();
    }
}
