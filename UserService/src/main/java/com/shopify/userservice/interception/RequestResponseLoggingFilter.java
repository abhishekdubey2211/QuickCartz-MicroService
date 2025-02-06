package com.shopify.userservice.interception;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestResponseLoggingFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}

		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
				(HttpServletResponse) response);

		chain.doFilter(wrappedRequest, wrappedResponse);

		// Log request and response AFTER the request has been processed
		logRequest(wrappedRequest);
		logResponse(wrappedResponse);

		// Ensure response body is properly written back
		wrappedResponse.copyBodyToResponse();
	}

	private Map<String, String> getRequestHeaders(HttpServletRequest request) {
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.put(headerName, request.getHeader(headerName));
		}
		return headers;
	}

	private Map<String, String> getResponseHeaders(HttpServletResponse response) {
		Map<String, String> headers = new HashMap<>();
		Collection<String> headerNames = response.getHeaderNames();
		for (String hname : headerNames) {
			headers.put(hname, response.getHeader(hname));
		}
		return headers;
	}

	private void logRequest(ContentCachingRequestWrapper request) {
		String requestBody = getRequestBody(request);
		log.info("*********Request Summary Details :" + LoggingInterceptor.requestId + "*******"
				+ "\nIncoming Request Method: " + request.getMethod() + " ## Incoming Request URL: "
				+ request.getRequestURL().toString() + " ## RequestId :  " + LoggingInterceptor.requestId
				+ "\nRequest Body: " + (requestBody.isEmpty() ? "No Body" : requestBody));
	}

	private void logResponse(ContentCachingResponseWrapper response) {
		String responseBody = getResponseBody(response);
		log.info("Outgoing Response Status: " + response.getStatus() + " ## Response Body: "
				+ (responseBody.isEmpty() ? "No Body" : responseBody) + "/nResponse Headers: "
				+ getResponseHeaders(response));
		log.info(
				"*********** Request Process Completed : RequestId :  " + LoggingInterceptor.requestId + "***********");
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private String getRequestBody(ContentCachingRequestWrapper request) {
		byte[] content = request.getContentAsByteArray();
		if (content.length == 0) {
			return "";
		}
		try {
			String json = new String(content,
					request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8");
			return objectMapper.writeValueAsString(objectMapper.readTree(json)); // Converts to compact JSON
		} catch (Exception e) {
			return "[Error: Invalid JSON or Unsupported Encoding]";
		}
	}

	private String getResponseBody(ContentCachingResponseWrapper response) {
		byte[] content = response.getContentAsByteArray();
		if (content.length == 0) {
			return "";
		}
		try {
			String json = new String(content,
					response.getCharacterEncoding() != null ? response.getCharacterEncoding() : "UTF-8");
			return objectMapper.writeValueAsString(objectMapper.readTree(json)); // Converts to compact JSON
		} catch (Exception e) {
			return "[Error: Invalid JSON or Unsupported Encoding]";
		}
	}
}
