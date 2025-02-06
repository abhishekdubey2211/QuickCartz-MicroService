package com.shopify.cartservice.dto;

import org.springframework.http.HttpStatus;

public record ErrorStatusDetails(int status, String statusdescription, HttpStatus errorStatus) {
}
