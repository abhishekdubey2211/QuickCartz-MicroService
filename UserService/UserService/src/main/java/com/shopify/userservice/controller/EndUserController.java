package com.shopify.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shopify.userservice.dto.EndUserDTO;
import com.shopify.userservice.dto.ResponseApi;
import com.shopify.userservice.service.EndUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User APIs", description = "Add , Read, Update & Delete User")
public class EndUserController {

	@Autowired
	private EndUserService endUserService;

	// Create a new user
	@PostMapping
    @Operation(summary = "Register user")
	public ResponseEntity<ResponseApi> registerEndUser(@RequestBody EndUserDTO endUserDTO) {
		EndUserDTO responseDto = endUserService.addUser(endUserDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseApi.createResponse(1, responseDto));
	}

	// Update an existing user
	@PutMapping
    @Operation(summary = "Update user")
	public ResponseEntity<ResponseApi> updateUser(@RequestBody EndUserDTO endUserDTO) {
		EndUserDTO updatedUserDto = endUserService.updateUser(endUserDTO);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseApi.createResponse(1, updatedUserDto));
	}

	// Get a user by ID
	@GetMapping("/{userId}")
    @Operation(summary = "Get User By Id")
	public ResponseEntity<ResponseApi> getUserById(@PathVariable long userId) {
		EndUserDTO user = endUserService.getUserById(userId);
		return ResponseEntity.ok(ResponseApi.createResponse(1, user));
	}

	// Get all users
	@GetMapping("/getall_users")
    @Operation(summary = "Get All Users")
	public ResponseEntity<ResponseApi> getAllUsers() {
		List<EndUserDTO> users = endUserService.getAllUsers();
		return ResponseEntity.ok(ResponseApi.createResponse(1, users));
	}
}