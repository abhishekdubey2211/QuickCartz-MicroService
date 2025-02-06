package com.shopify.userservice.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.shopify.userservice.dto.CartItemDto;
import com.shopify.userservice.dto.ResponseApi;


@Component
@FeignClient(name = "CART-SERVICE" , path = "/api/cart")
public interface CartProxy {

	@PostMapping("/create/{userid}")
	public ResponseEntity<ResponseApi> createCartForUser(@PathVariable("userid") Long userid);

	@PostMapping("/cart/{userid}")
	public ResponseEntity<ResponseApi> addProductToCart(@RequestBody CartItemDto cartItem,
			@PathVariable("userid") Long userid);
}
