package com.shopify.productservice.dto;
import org.springframework.http.HttpStatus;

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
public class ProductImagesDTO {
	private long imageId;
	private String imageUrl;
	
	public ErrorStatusDetails validateProductImageRequest(ProductImagesDTO productImagesDTO) {
	    // Validate image ID
	    if (productImagesDTO.getImageId() <= 0) {
	        return new ErrorStatusDetails(5017, "Image ID must be a positive number", HttpStatus.BAD_REQUEST);
	    }

	    // Validate image URL
	    if (productImagesDTO.getImageUrl() == null || productImagesDTO.getImageUrl().trim().isEmpty()) {
	        return new ErrorStatusDetails(5018, "Image URL is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productImagesDTO.getImageUrl().length() > 255) {
	        return new ErrorStatusDetails(5019, "Image URL must not exceed 255 characters", HttpStatus.BAD_REQUEST);
	    } else {
	        String urlPattern = "^(https?:\\/\\/)?([\\w-]+\\.)+[\\w-]+(\\/[^\\s]*)?$";
	        if (!productImagesDTO.getImageUrl().matches(urlPattern)) {
	            return new ErrorStatusDetails(5020, "Invalid image URL format", HttpStatus.BAD_REQUEST);
	        }
	    }
	    return null;
	}

}