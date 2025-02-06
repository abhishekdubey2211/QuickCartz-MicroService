package com.shopify.userservice.service;
import java.util.List;

import com.shopify.userservice.dto.RessidentialAddressDTO;

public interface AddressService {

	RessidentialAddressDTO addAddress(RessidentialAddressDTO ressidentialAddressDTO,Long userId);

	RessidentialAddressDTO updateAddress(RessidentialAddressDTO ressidentialAddressDTO,Long userId);

	RessidentialAddressDTO getAddressById(long addressId,Long userId);

	List<RessidentialAddressDTO> getAllAddress(Long userId);
}
