package com.shopify.userservice.mapper;

import java.time.LocalDate;
import java.util.ArrayList;

import com.shopify.userservice.dto.EndUserDTO;
import com.shopify.userservice.dto.RessidentialAddressDTO;
import com.shopify.userservice.dto.UserParameterDetailsDTO;
import com.shopify.userservice.model.EndUser;
import com.shopify.userservice.model.RessidentialAddress;
import com.shopify.userservice.model.UserParameterDetails;

public class Mapper {

	public EndUserDTO convertToUserDTO(EndUser endUser) {
		EndUserDTO dto = new EndUserDTO();
		dto.setUserId(endUser.getId());
		dto.setUserName(endUser.getUsername());
		dto.setPassword(endUser.getPassword());
		dto.setConfirmPassword(endUser.getPassword());
		dto.setDesignation(endUser.getDesignation());
		dto.setEmailAddress(endUser.getEmail());
		dto.setContactNumber(endUser.getContact());
		dto.setProfileImage(endUser.getProfileimage());
		dto.setDateOfBirth(endUser.getDateofbirth());
		dto.setRoles(endUser.getRoles());

		// Convert the list of residential addresses
		if (endUser.getAddress() != null) {
			dto.setResidentialAddress(endUser.getAddress().stream().map(this::convertToAddressDTO).toList());
		} else {
			dto.setResidentialAddress(new ArrayList<>());
		}

		// Convert the list of user parameter details
		if (endUser.getUserParameterDetails() != null) {
			dto.setUserParameterDetails(
					endUser.getUserParameterDetails().stream().map(this::convertToUserParameterDetailsDTO).toList());
		} else {
			dto.setUserParameterDetails(new ArrayList<>());
		}

		return dto;
	}

	public EndUser convertToUserEntity(EndUserDTO endUserDTO) {
		EndUser endUser = EndUser.builder().id(endUserDTO.getUserId()).username(endUserDTO.getUserName())
				.password(endUserDTO.getConfirmPassword()).designation(endUserDTO.getDesignation())
				.email(endUserDTO.getEmailAddress()).contact(endUserDTO.getContactNumber())
				.profileimage(endUserDTO.getProfileImage()).dateofbirth(endUserDTO.getDateOfBirth())
				.roles(endUserDTO.getRoles()).build();

		// Convert residential addresses
		endUser.setAddress(endUserDTO.getResidentialAddress() != null ? endUserDTO.getResidentialAddress().stream()
				.map(this::convertToAddressEntity).peek(address -> address.setUser(endUser)) // Set the parent reference
				.toList() : new ArrayList<>());

		// Convert user parameter details
		endUser.setUserParameterDetails(endUserDTO.getUserParameterDetails() != null
				? endUserDTO.getUserParameterDetails().stream().map(this::convertToUserParameterDetailsEntity)
						.peek(parameterDetails -> parameterDetails.setUser(endUser)) // Set the parent reference
						.toList()
				: new ArrayList<>());
		endUser.setUsercreationdate(LocalDate.now().toString());
		endUser.setUserlastupdatedate(LocalDate.now().toString());
		return endUser;
	}

	public RessidentialAddress convertToAddressEntity(RessidentialAddressDTO dto) {
		return RessidentialAddress.builder().id(dto.getAddressId()).address1(dto.getAddress1())
				.address2(dto.getAddress2()).village(dto.getVillage()).state(dto.getState()).city(dto.getCity())
				.pincode(dto.getPincode()).isPrimary(dto.getIsPrimary() == 1).build();
	}

	public RessidentialAddressDTO convertToAddressDTO(RessidentialAddress entity) {
		RessidentialAddressDTO dto = new RessidentialAddressDTO();
		dto.setAddressId(entity.getId());
		dto.setAddress1(entity.getAddress1());
		dto.setAddress2(entity.getAddress2());
		dto.setVillage(entity.getVillage());
		dto.setState(entity.getState());
		dto.setCity(entity.getCity());
		dto.setPincode(entity.getPincode());
		dto.setIsPrimary(entity.isPrimary() ? 1 : 0);
		return dto;
	}

	public UserParameterDetailsDTO convertToUserParameterDetailsDTO(UserParameterDetails entity) {
		return UserParameterDetailsDTO.builder().parameterId(entity.getParameterid()).srNo(entity.getSrno())
				.value(entity.getValue()).description(entity.getDescription()).build();
	}

	public UserParameterDetails convertToUserParameterDetailsEntity(UserParameterDetailsDTO dto) {
		return UserParameterDetails.builder().parameterid(dto.getParameterId()).srno(dto.getSrNo())
				.value(dto.getValue()).description(dto.getDescription()).build();
	}

}
