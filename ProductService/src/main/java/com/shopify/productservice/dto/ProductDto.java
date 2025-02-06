package com.shopify.productservice.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.shopify.productservice.model.ProductImages;
import com.shopify.productservice.model.ProductSpecificationDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductDto {
	private Long productId;
	private String productName;
	private String brandName;
	private String categoryName;
	private String productModel;
	private double currentDiscount;
	private double marketPrice;
	private double price;
	private int productQuantity;
	private String description;
	private String productStatus;	
	private List<ProductImagesDTO> images;
	private List<ProductSpecificationDTO> productspecification;
	
	public ErrorStatusDetails validateProductRequest(ProductDto productDto, String operation) {
	    // Validate product name
	    if (productDto.getProductName() == null || productDto.getProductName().trim().isEmpty()) {
	        return new ErrorStatusDetails(5001, "Product name is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productDto.getProductName().length() > 200) {
	        return new ErrorStatusDetails(5002, "Product name must not exceed 200 characters", HttpStatus.BAD_REQUEST);
	    }

	    // Validate brand name
	    if (productDto.getBrandName() == null || productDto.getBrandName().trim().isEmpty()) {
	        return new ErrorStatusDetails(5003, "Brand name is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productDto.getBrandName().length() > 100) {
	        return new ErrorStatusDetails(5004, "Brand name must not exceed 100 characters", HttpStatus.BAD_REQUEST);
	    }

	    // Validate category name
	    if (productDto.getCategoryName() == null || productDto.getCategoryName().trim().isEmpty()) {
	        return new ErrorStatusDetails(5005, "Category name is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productDto.getCategoryName().length() > 100) {
	        return new ErrorStatusDetails(5006, "Category name must not exceed 100 characters", HttpStatus.BAD_REQUEST);
	    }

	    // Validate product model
	    if (productDto.getProductModel() == null || productDto.getProductModel().trim().isEmpty()) {
	        return new ErrorStatusDetails(5007, "Product model is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productDto.getProductModel().length() > 150) {
	        return new ErrorStatusDetails(5008, "Product model must not exceed 150 characters", HttpStatus.BAD_REQUEST);
	    }

	    // Validate discount
	    if (productDto.getCurrentDiscount() < 0 || productDto.getCurrentDiscount() > 100) {
	        return new ErrorStatusDetails(5009, "Discount must be between 0 and 100", HttpStatus.BAD_REQUEST);
	    }

	    // Validate market price
	    if (productDto.getMarketPrice() <= 0) {
	        return new ErrorStatusDetails(5010, "Market price must be positive", HttpStatus.BAD_REQUEST);
	    }

//	    // Validate price
//	    if (productDto.getPrice() <= 0) {
//	        return new ErrorStatusDetails(5011, "Price must be positive", HttpStatus.BAD_REQUEST);
//	    }

	    // Validate quantity
	    if (productDto.getProductQuantity() < 0) {
	        return new ErrorStatusDetails(5012, "Quantity cannot be negative", HttpStatus.BAD_REQUEST);
	    }

	    // Validate description
	    if (productDto.getDescription() == null || productDto.getDescription().trim().isEmpty()) {
	        return new ErrorStatusDetails(5013, "Description is mandatory", HttpStatus.BAD_REQUEST);
	    } else if (productDto.getDescription().length() > 500) {
	        return new ErrorStatusDetails(5014, "Description must not exceed 500 characters", HttpStatus.BAD_REQUEST);
	    }

//	    // Validate product status
//	    if (productDto.getProductStatus() == null || productDto.getProductStatus().trim().isEmpty()) {
//	        return new ErrorStatusDetails(5015, "Product status is mandatory", HttpStatus.BAD_REQUEST);
//	    }

	    return null;
	}

}
