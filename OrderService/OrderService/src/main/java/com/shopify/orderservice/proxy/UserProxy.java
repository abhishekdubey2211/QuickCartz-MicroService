package com.shopify.orderservice.proxy;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shopify.orderservice.dto.EndUserDTO;
import com.shopify.orderservice.dto.ResponseApi;


@Component
@FeignClient(name = "USER-SERVICE" , path = "/api/user")
public interface UserProxy {

	// Create a new user
	@PostMapping
	public ResponseEntity<ResponseApi> registerEndUser(@RequestBody EndUserDTO endUserDTO);

	// Update an existing user
	@PutMapping
	public ResponseEntity<ResponseApi> updateUser(@RequestBody EndUserDTO endUserDTO);

	// Get a user by ID
	@GetMapping("/{userId}")
	public ResponseEntity<ResponseApi> getUserById(@PathVariable long userId);

	// Get all users
	@GetMapping("/getall_users")
	public ResponseEntity<ResponseApi> getAllUsers();
}
