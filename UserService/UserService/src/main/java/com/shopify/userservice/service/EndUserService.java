package com.shopify.userservice.service;

import java.util.List;

import com.shopify.userservice.dto.EndUserDTO;
import com.shopify.userservice.model.EndUser;

public interface EndUserService {

	EndUserDTO addUser(EndUserDTO endUserDTO);

	EndUserDTO updateUser(EndUserDTO endUserDTO);

	EndUserDTO getUserById(long userId);

	List<EndUserDTO> getAllUsers();

}
