package com.shopify.userservice.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.shopify.userservice.exception.CustomException;
import com.shopify.userservice.dto.EndUserDTO;
import com.shopify.userservice.dto.ErrorStatusDetails;
import com.shopify.userservice.mapper.Mapper;
import com.shopify.userservice.model.EndUser;
import com.shopify.userservice.model.UserParameterDetails;
import com.shopify.userservice.proxy.CartProxy;
import com.shopify.userservice.repository.EndUserRepository;
import com.shopify.userservice.service.EndUserService;

@Service
public class EndUserImplementation implements EndUserService {
	private static final Logger log = LoggerFactory.getLogger(EndUserImplementation.class);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private Mapper mapper = new Mapper();
	private EndUserRepository endUserRepository;

	@Autowired
	CartProxy cartProxy;
	
	@Autowired
	public EndUserImplementation(EndUserRepository endUserRepository) {
		super();
		this.endUserRepository = endUserRepository;
	}

	@Override
	public EndUserDTO addUser(EndUserDTO endUserDTO) {
		ErrorStatusDetails error = endUserDTO.validateEndUserRequest(endUserDTO, "add");
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		// Convert DTO to Entity
		EndUser endUser = mapper.convertToUserEntity(endUserDTO);
		checkUserEmailExists(endUser);
		endUser.setIsactive(1);
		endUser.setIsdelete(0);

		// get password and encrypt before storing in db

		// Set creation and update dates
		endUser.setUsercreationdate(LocalDateTime.now().format(formatter));
		endUser.setUserlastupdatedate("");

		// Map Residential Address
		Optional.ofNullable(endUserDTO.getResidentialAddress()).ifPresent(addresses -> {
			endUser.setAddress(addresses.stream().map(mapper::convertToAddressEntity).toList());
			endUser.getAddress().forEach(a -> a.setUser(endUser));
		});

		// Map User Parameter Details
		Optional.ofNullable(endUserDTO.getUserParameterDetails()).ifPresent(details -> {
			endUser.setUserParameterDetails(details.stream().map(mapper::convertToUserParameterDetailsEntity).toList());
			endUser.getUserParameterDetails().forEach(a -> a.setUser(endUser));
		});
		List<UserParameterDetails> userDetails = new ArrayList<>(endUser.getUserParameterDetails());
		userDetails.add(new UserParameterDetails(101, 1, UUID.randomUUID().toString(), "User Unique Id", endUser));
		endUser.setUserParameterDetails(userDetails);
		
		// Save and return DTO
		EndUserDTO dto= mapper.convertToUserDTO(endUserRepository.save(endUser));
		cartProxy.createCartForUser(dto.getUserId());
		return dto;
	}

	@Override
	public EndUserDTO updateUser(EndUserDTO endUserDTO) {
		ErrorStatusDetails error = endUserDTO.validateEndUserRequest(endUserDTO, "edit");
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		if (endUserDTO.getUserId() <= 0) {
			throw new CustomException(4041, "BAD_REQUEST", "User Id not found");
		}

		// Convert DTO to Entity
		EndUser updateEndUser = mapper.convertToUserEntity(endUserDTO);

		EndUser existingEndUser = getSingleUserById(updateEndUser.getId());

		if (!existingEndUser.getEmail().equals(updateEndUser.getEmail())) {
			checkUserEmailExists(updateEndUser);
			existingEndUser.setEmail(updateEndUser.getEmail());
		}

		existingEndUser.setContact(updateEndUser.getContact());
		existingEndUser.setDateofbirth(updateEndUser.getDateofbirth());
		existingEndUser.setDesignation(updateEndUser.getDesignation());
		existingEndUser.setUserlastupdatedate(LocalDateTime.now().format(formatter));
//		existingEndUser.setRoles(updateEndUser.getRoles());
		existingEndUser.setProfileimage(updateEndUser.getProfileimage());
		existingEndUser.getAddress().clear();
		updateEndUser.getAddress().forEach(address -> {
			address.setUser(existingEndUser);
			existingEndUser.getAddress().add(address);
		});
		if (updateEndUser.getUserParameterDetails() != null) {
			List<UserParameterDetails> userParameter = existingEndUser.getUserParameterDetails().stream()
					.filter(parameter -> parameter.getParameterid() == 101).collect(Collectors.toList());
			existingEndUser.getUserParameterDetails().clear();
			for (UserParameterDetails parameter : updateEndUser.getUserParameterDetails()) {
				parameter.setUser(existingEndUser);
				existingEndUser.getUserParameterDetails().add(parameter);
			}
			existingEndUser.getUserParameterDetails().addAll(userParameter);
		}
		return mapper.convertToUserDTO(endUserRepository.save(existingEndUser));
	}

	@Override
	public EndUserDTO getUserById(long userId) {
		EndUser existingEndUser = getSingleUserById(userId);
		return mapper.convertToUserDTO(endUserRepository.save(existingEndUser));
	}

	@Override
	public List<EndUserDTO> getAllUsers() {
		List<EndUser> endActiveUsersList = endUserRepository.findAll().stream()
				.filter((user) -> user.getIsdelete() == 0 && user.getIsactive() == 1).collect(Collectors.toList());
		return endActiveUsersList.stream().map(mapper::convertToUserDTO).collect(Collectors.toList());
	}

	public EndUser getSingleUserById(long userId) {
		try {
			EndUser existingEndUser = endUserRepository.findById(userId).orElseThrow(
					() -> new CustomException(4041, "BAD_REQUEST", "User with Userid " + userId + " does not exist"));

			if (existingEndUser.getIsdelete() == 1 && existingEndUser.getIsactive() == 0) {
				throw new CustomException(4041, "BAD_REQUEST", "User with Userid " + userId + " does not exist");
			}
			return existingEndUser;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean checkUserEmailExists(EndUser pushUser) {
		Optional<EndUser> existingUser = endUserRepository.findByEmail(pushUser.getEmail());
		if (existingUser.isPresent()) {
			if (existingUser.get().getIsdelete() == 0) {
				throw new CustomException(4040, "BAD_REQUEST",
						"User with Email " + pushUser.getEmail() + " already exists");
			}
		}
		return true;
	}
}
