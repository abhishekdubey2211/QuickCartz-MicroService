package com.shopify.orderservice.dto;

import java.util.List;
import org.springframework.http.HttpStatus;
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
	
}
