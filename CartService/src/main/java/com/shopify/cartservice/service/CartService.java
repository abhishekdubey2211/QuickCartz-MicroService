package com.shopify.cartservice.service;

import com.shopify.cartservice.dto.CartDto;
import com.shopify.cartservice.dto.CartItemDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {

    CartDto createCartForUser(long UserId);

    CartDto addProductToCart(Long userid, CartItemDto pushCartItemDTO);

    @Transactional
    CartDto removeItemFromCart(Long userid, Long productid);

    @Transactional
    CartDto removeAllProducts(Long userid);

    List<CartItemDto> getAllCartItems(Long userid);
}
