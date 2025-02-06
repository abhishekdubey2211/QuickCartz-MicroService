package com.shopify.orderservice.dto;

import org.springframework.http.HttpStatus;

public record ErrorStatusDetails(int status, String statusdescription, HttpStatus errorStatus) {
}
