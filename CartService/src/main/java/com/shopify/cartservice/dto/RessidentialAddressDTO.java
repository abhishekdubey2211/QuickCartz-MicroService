package com.shopify.cartservice.dto;

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

	public ErrorStatusDetails validateResidentialAddressDTO(RessidentialAddressDTO addressDTO) {
		// Validate address1
		if (addressDTO.getAddress1() == null || addressDTO.getAddress1().trim().isEmpty()) {
			return new ErrorStatusDetails(4020, "Address line 1 is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (addressDTO.getAddress1().length() > 200) {
			return new ErrorStatusDetails(4021, "Address line 1 cannot exceed 200 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate address2
		if (addressDTO.getAddress2() != null && addressDTO.getAddress2().length() > 200) {
			return new ErrorStatusDetails(4022, "Address line 2 cannot exceed 200 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate village
		if (addressDTO.getVillage() != null && addressDTO.getVillage().length() > 100) {
			return new ErrorStatusDetails(4023, "Village name cannot exceed 100 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate state
		if (addressDTO.getState() == null || addressDTO.getState().trim().isEmpty()) {
			return new ErrorStatusDetails(4024, "State is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (addressDTO.getState().length() > 100) {
			return new ErrorStatusDetails(4025, "State cannot exceed 100 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate city
		if (addressDTO.getCity() == null || addressDTO.getCity().trim().isEmpty()) {
			return new ErrorStatusDetails(4026, "City is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (addressDTO.getCity().length() > 100) {
			return new ErrorStatusDetails(4027, "City cannot exceed 100 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate pincode
		if (addressDTO.getPincode() == null || addressDTO.getPincode().trim().isEmpty()) {
			return new ErrorStatusDetails(4028, "Pincode is mandatory", HttpStatus.BAD_REQUEST);
		}
		
		if (addressDTO.getPincode().length() != 6 || !addressDTO.getPincode().matches("\\d{6}")) {
			return new ErrorStatusDetails(4029, "Pincode must be exactly 6 numeric digits", HttpStatus.BAD_REQUEST);
		}

		// Validate isPrimary
		if (addressDTO.getIsPrimary() < 0 || addressDTO.getIsPrimary() > 1) {
			return new ErrorStatusDetails(4030, "isPrimary must be 0 or 1", HttpStatus.BAD_REQUEST);
		}

		// All validations passed
		return null;
	}
}
