package com.shopify.loginservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest {
	private String loginId;
	private String loginPassword;
	private String userLanguage;
	private String loginSessionKey;
	private String authenticationKey;
	private String otp;
	private String loginMethod;
}
