/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shopify.cartservice.controller;

import com.shopify.cartservice.dto.CartDto;
import com.shopify.cartservice.dto.CartItemDto;
import com.shopify.cartservice.dto.ResponseApi;
import com.shopify.cartservice.service.CartService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author avhan
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
  
	@Autowired
	private CartService cartService;

        @PostMapping("/create/{userid}")
	public ResponseEntity<ResponseApi> createCartForUser(
			@PathVariable("userid") Long userid) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseApi.createResponse(1, cartService.createCartForUser(userid)));
	}
        
	@PostMapping("/{userid}")
	public ResponseEntity<ResponseApi> addProductToCart(@RequestBody CartItemDto cartItem,
			@PathVariable("userid") Long userid) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseApi.createResponse(1, cartService.addProductToCart(userid, cartItem)));
	}

	@DeleteMapping("/{userid}/{productid}")
	public ResponseEntity<ResponseApi> removeProductFromCart(@PathVariable("userid") Long userid,
			@PathVariable("productid") Long productid) throws Exception {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, cartService.removeItemFromCart(userid, productid)));
	}

	@DeleteMapping("/flushproduct/{userid}")
	public ResponseEntity<ResponseApi> removeAllProductsFromCart(@PathVariable("userid") Long userid) throws Exception {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1,  cartService.removeAllProducts(userid)));
	}

	@GetMapping("/get_products/{userid}")
	public ResponseEntity<ResponseApi> getUserCartDetails(@PathVariable("userid") Long userid) throws Exception {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1,  cartService.getAllCartItems(userid)));
	}
}