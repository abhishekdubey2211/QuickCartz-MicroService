package com.shopify.userservice.service.implementation;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.shopify.userservice.dto.ErrorStatusDetails;
import com.shopify.userservice.dto.RessidentialAddressDTO;
import com.shopify.userservice.exception.CustomException;
import com.shopify.userservice.mapper.Mapper;
import com.shopify.userservice.model.EndUser;
import com.shopify.userservice.model.RessidentialAddress;
import com.shopify.userservice.repository.RessidentialAddressRepository;
import com.shopify.userservice.service.AddressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RessidentialAddressImplementation implements AddressService {
	private Mapper mapper = new Mapper();
	private final RessidentialAddressRepository addressRepository;

	@Autowired
	EndUserImplementation endUserImplementation;

	@Autowired
	public RessidentialAddressImplementation(RessidentialAddressRepository addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	@Override
	public RessidentialAddressDTO addAddress(RessidentialAddressDTO ressidentialAddressDTO, Long userId) {
		ErrorStatusDetails error = ressidentialAddressDTO.validateResidentialAddressDTO(ressidentialAddressDTO);
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		EndUser endUser = endUserImplementation.getSingleUserById(userId);
		RessidentialAddress entity = mapper.convertToAddressEntity(ressidentialAddressDTO);
		entity.setUser(endUser);
		RessidentialAddress savedEntity = addressRepository.save(entity);
		log.info("Residential address added successfully: {}", savedEntity);
		return mapper.convertToAddressDTO(savedEntity);
	}

	@Override
	public RessidentialAddressDTO updateAddress(RessidentialAddressDTO ressidentialAddressDTO, Long userId) {
		ErrorStatusDetails error = ressidentialAddressDTO.validateResidentialAddressDTO(ressidentialAddressDTO);
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		RessidentialAddress existingAddress = addressRepository.findById(ressidentialAddressDTO.getAddressId())
				.orElseThrow(
						() -> new CustomException(4043, "NOT_FOUND", "Address with the provided ID doesn't exist."));

		if (!(existingAddress.getUser().getId() == userId)) {
			throw new CustomException(403, "FORBIDDEN", "This address does not belong to the provided user.");
		}
		EndUser endUser = endUserImplementation.getSingleUserById(userId);
		RessidentialAddress updatedEntity = mapper.convertToAddressEntity(ressidentialAddressDTO);
		updatedEntity.setId(existingAddress.getId());
		updatedEntity.setUser(endUser);
		RessidentialAddress savedEntity = addressRepository.save(updatedEntity);
		return mapper.convertToAddressDTO(savedEntity);
	}

	@Override
	public RessidentialAddressDTO getAddressById(long addressId, Long userId) {
		EndUser endUser = endUserImplementation.getSingleUserById(userId);
		RessidentialAddress address = addressRepository.findById(addressId).orElseThrow(
				() -> new CustomException(4044, "NOT_FOUND", "Address with the provided ID doesn't exist."));

		if ((address.getUser().getId() != userId)) {
			throw new CustomException(403, "FORBIDDEN", "This address does not belong to the provided user.");
		}
		return mapper.convertToAddressDTO(address);
	}

	@Override
	public List<RessidentialAddressDTO> getAllAddress(Long userId) {
		EndUser endUser = endUserImplementation.getSingleUserById(userId);
		List<RessidentialAddress> addresses = endUser.getAddress();
		if (addresses.isEmpty()) {
			throw new CustomException(4045, "NOT_FOUND", "No addresses found for the provided user.");
		}
		return addresses.stream().map(mapper::convertToAddressDTO).collect(Collectors.toList());
	}

}
