package com.shopify.cartserviceinterceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String requestId = null;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// Generate a unique requestId for each incoming request
		requestId = UUID.randomUUID().toString();

		// Log the request method, URL, and headers
		log.info("*********** Incoming Started to Process : RequestId :  " + requestId + "******************"
				+ "\n## Request Method: " + request.getMethod() + " ## Request URL: " + getRequestUrl(request)
				+ " ## Request DateTime: " + LocalDateTime.now().format(formatter) + " ## Unique RequestID: "
				+ requestId + "\nHeaders: " + getAdditionalHeaders(request));

		// Set the unique requestId in the response header
		response.setHeader("X-Request-ID", requestId);

		return true; // Continue with the request
	}

	private String getRequestUrl(HttpServletRequest request) {
		// This ensures we get the correct URL without repeating the context path
		return request.getRequestURL().toString();
	}

	private Map<String, String> getAdditionalHeaders(HttpServletRequest request) {
		Map<String, String> additionalHeaders = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			additionalHeaders.put(headerName, request.getHeader(headerName));
		}
		return additionalHeaders;
	}
}
