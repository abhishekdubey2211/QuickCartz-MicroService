package com.shopify.orderservice.dto;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RessidentialAddressDTO {

	private long addressId;
	private String address1;
	private String address2;
	private String village;
	private String state;
	private String city;
	private String pincode;
	private int isPrimary;

}
