package com.shopify.productservice.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class ProductSpecificationDTO {
	private int fieldId;
	private int srNo;
	private String fieldName;
	private String fieldValue;

	public ErrorStatusDetails validateProductSpecificationRequest(ProductSpecificationDTO productSpecificationDTO) {
		// Validate field ID
		if (productSpecificationDTO.getFieldId() <= 0) {
			return new ErrorStatusDetails(5021, "Field ID must be a positive number", HttpStatus.BAD_REQUEST);
		}

		// Validate serial number
		if (productSpecificationDTO.getSrNo() <= 0) {
			return new ErrorStatusDetails(5022, "Serial number must be a positive number", HttpStatus.BAD_REQUEST);
		}

		// Validate field name
		if (productSpecificationDTO.getFieldName() == null || productSpecificationDTO.getFieldName().trim().isEmpty()) {
			return new ErrorStatusDetails(5023, "Field name is mandatory", HttpStatus.BAD_REQUEST);
		} else if (productSpecificationDTO.getFieldName().length() > 100) {
			return new ErrorStatusDetails(5024, "Field name must not exceed 100 characters", HttpStatus.BAD_REQUEST);
		}

		// Validate field value
		if (productSpecificationDTO.getFieldValue() == null
				|| productSpecificationDTO.getFieldValue().trim().isEmpty()) {
			return new ErrorStatusDetails(5026, "Field value is mandatory", HttpStatus.BAD_REQUEST);
		} else if (productSpecificationDTO.getFieldValue().length() > 255) {
			return new ErrorStatusDetails(5025, "Field value must not exceed 255 characters", HttpStatus.BAD_REQUEST);
		}

		return null;
	}

}