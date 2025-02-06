package com.shopify.orderservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.context.request.WebRequest;
import java.net.http.HttpHeaders;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.shopify.orderservice.dto.ResponseApi;

//import com.shopify.productservice.interceptors.LoggingInterceptor;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ResponseApi> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				"Could not execute statement: Duplicate entry [" + ex.getMessage() + "]", request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResponseApi> handleConstraintViolationException(ConstraintViolationException ex,
			HttpServletRequest request) {
		StringBuilder violations = new StringBuilder();
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		for (ConstraintViolation<?> violation : constraintViolations) {
			violations.append(violation.getMessage()).append("; ");
		}
		ResponseApi errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, violations.toString(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			org.springframework.http.HttpHeaders headers, HttpStatus status,
			org.springframework.web.context.request.WebRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed",
				request.getContextPath());

		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		});
//		errorDetails.put("fieldErrors", fieldErrors);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException ex, HttpServletRequest request) {
		if (ex.getStatusCode() > 0) {
			if (ex.getStatus() == null) {
				Map<String, Object> errorDetails = createCustomErrorResponse(ex.getStatusCode(),
						ex.getStatusDescription(), ex.getMessage(), request.getRequestURI());
				return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
			}
			Map<String, Object> errorDetails = createCustomErrorResponse(ex.getStatusCode(), ex.getStatusDescription(),
					ex.getMessage(), request.getRequestURI());
			return new ResponseEntity<>(errorDetails, ex.getStatus());
		}
		ResponseApi errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseApi> handleGlobalException(Exception ex, HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ResponseApi> handleNullPointerException(NullPointerException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseApi> handleIllegalArgumentException(IllegalArgumentException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResponseApi> handleResourceNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NoSuchMethodException.class)
	public ResponseEntity<ResponseApi> handleNoSuchMethodException(NoSuchMethodException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
				"Method not found: " + ex.getMessage(), request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseApi> handleAccessDeniedException(AccessDeniedException ex,
			HttpServletRequest request) {
		ResponseApi errorDetails = createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
	}

//	private Map<String, Object> createErrorResponse(HttpStatus status, String message, String path) {
//		Map<String, Object> errorDetails = new LinkedHashMap<>();
//		errorDetails.put("responseDateTime", SF.format(new Date()));
//		errorDetails.put("status", status.value());
//		errorDetails.put("statusDescription", status.getReasonPhrase());
//		errorDetails.put("message", message);
////		errorDetails.put("revisionId", LoggingInterceptor.requestId);
////		errorDetails.put("path", path);
//		return errorDetails;
//	}
	private ResponseApi createErrorResponse(HttpStatus status, String message, String path) {
		ResponseApi errorDetails = new ResponseApi();
		errorDetails.setResponseDateTime(SF.format(new Date()));
		errorDetails.setStatus(status.value());
		errorDetails.setStatusDescription(status.getReasonPhrase());
		errorDetails.setMessage(message);
//		errorDetails.put("revisionId", LoggingInterceptor.requestId);
//		errorDetails.put("path", path);
		return errorDetails;
	}

	private Map<String, Object> createCustomErrorResponse(int status, String statusDescription, String message,
			String path) {
		Map<String, Object> errorDetails = new LinkedHashMap<>();
		errorDetails.put("responseDateTime", SF.format(new Date()));
		errorDetails.put("status", status);
		errorDetails.put("statusDescription", statusDescription);
		errorDetails.put("message", message);
//		errorDetails.put("revisionId", LoggingInterceptor.requestId);
//		errorDetails.put("path", path);
		return errorDetails;
	}
}
