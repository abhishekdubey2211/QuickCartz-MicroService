package com.shopify.userservice.controller;
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
import java.util.List;
import com.shopify.userservice.dto.ResponseApi;
import com.shopify.userservice.dto.RessidentialAddressDTO;
import com.shopify.userservice.exception.CustomException;
import com.shopify.userservice.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/address")
@Slf4j
@Tag(name = "User APIs", description = "Add , Read, Update & Delete Address")
public class RessidentialAddressController {

	@Autowired
	private AddressService addressService;

	@PostMapping("/user/{userId}")
    @Operation(summary = "Add Address of an User")
	public ResponseEntity<ResponseApi> addAddress(@PathVariable long userId,
			@RequestBody RessidentialAddressDTO ressidentialAddressDTO) {
		RessidentialAddressDTO addedAddress = addressService.addAddress(ressidentialAddressDTO, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseApi.createResponse(1, addedAddress));
	}
	
	@PutMapping("/user/{userId}")
    @Operation(summary = "Update Address of an User")
	public ResponseEntity<ResponseApi> updateAddress(@PathVariable long userId,
	        @RequestBody RessidentialAddressDTO ressidentialAddressDTO) {
	    RessidentialAddressDTO updatedAddress = addressService.updateAddress(ressidentialAddressDTO, userId);
	    return ResponseEntity.status(HttpStatus.OK).body(ResponseApi.createResponse(1, updatedAddress));
	}

	@GetMapping("/user/{userId}/{addressId}")
    @Operation(summary = "Get Address by Id of an User")
	public ResponseEntity<ResponseApi> getAddressById(@PathVariable("userId") long userId, @PathVariable("addressId") long addressId) {
		RessidentialAddressDTO addressDTO = addressService.getAddressById(addressId, userId);
		return ResponseEntity.status(HttpStatus.OK).body(ResponseApi.createResponse(1, addressDTO));
	}

	@GetMapping("/user/{userId}")
    @Operation(summary = "Get All Addresses of User")
	public ResponseEntity<ResponseApi> getAllAddresses(@PathVariable long userId) {
		List<RessidentialAddressDTO> addresses = addressService.getAllAddress(userId);
		return ResponseEntity.status(HttpStatus.OK).body(ResponseApi.createResponse(1, addresses));
	}
}
