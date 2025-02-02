package com.shopify.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.userservice.model.EndUser;

public interface EndUserRepository extends JpaRepository<EndUser, Long> {

	Optional<EndUser> findByEmail(String strEmailAddress);

}
