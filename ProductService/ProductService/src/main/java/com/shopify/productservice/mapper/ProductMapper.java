package com.shopify.productservice.mapper;

import java.util.List;
import java.util.stream.Collectors;
import com.shopify.productservice.dto.ProductDto;
import com.shopify.productservice.dto.ProductImagesDTO;
import com.shopify.productservice.dto.ProductSpecificationDTO;
import com.shopify.productservice.model.Product;
import com.shopify.productservice.model.ProductImages;
import com.shopify.productservice.model.ProductSpecificationDetails;

public class ProductMapper {

	public static ProductDto toProductDto(Product product) {
		if (product == null) {
			return null;
		}
		return ProductDto.builder().productId(product.getId()).productName(product.getName())
				.brandName(product.getBrand()).categoryName(product.getCategory()).productModel(product.getModel())
				.currentDiscount(product.getDiscount()).marketPrice(product.getMarketprice()).price(product.getPrice())
				.productQuantity(product.getQuantity()).description(product.getDescription())
				.productStatus(product.getQuantity() > 0 ? "IN-STOCK" : "OUTOFF-STOCK")
				.images(toProductImagesDTOList(product.getImages()))
				.productspecification(toProductSpecificationDTOList(product.getProductspecification())).build();
	}

	public static Product toProductEntity(ProductDto productDto) {
		if (productDto == null) {
			return null;
		}
		return Product.builder().id(productDto.getProductId()).name(productDto.getProductName())
				.brand(productDto.getBrandName()).category(productDto.getCategoryName())
				.model(productDto.getProductModel()).discount(productDto.getCurrentDiscount())
				.marketprice(productDto.getMarketPrice()).price(productDto.getPrice())
				.quantity(productDto.getProductQuantity()).description(productDto.getDescription())
				.instock(productDto.getProductQuantity())
				.productstatus(productDto.getProductQuantity() > 0 ? "IN-STOCK" : "OUTOFF-STOCK")
				.images(toProductImagesEntityList(productDto.getImages()))
				.productspecification(toProductSpecificationEntityList(productDto.getProductspecification())).build();
	}

	public static ProductImagesDTO toProductImagesDTO(ProductImages image) {
		if (image == null) {
			return null;
		}

		return ProductImagesDTO.builder().imageId(image.getId()).imageUrl(image.getUrl()).build();
	}

	public static List<ProductImagesDTO> toProductImagesDTOList(List<ProductImages> images) {
		if (images == null) {
			return null;
		}
		return images.stream().map(ProductMapper::toProductImagesDTO).collect(Collectors.toList());
	}

	public static ProductImages toProductImagesEntity(ProductImagesDTO imageDTO) {
		if (imageDTO == null) {
			return null;
		}
		return ProductImages.builder().id(imageDTO.getImageId()).url(imageDTO.getImageUrl()).build();
	}

	public static List<ProductImages> toProductImagesEntityList(List<ProductImagesDTO> imageDTOs) {
		if (imageDTOs == null) {
			return null;
		}

		return imageDTOs.stream().map(ProductMapper::toProductImagesEntity).collect(Collectors.toList());
	}

	public static ProductSpecificationDTO toProductSpecificationDTO(ProductSpecificationDetails spec) {
		if (spec == null) {
			return null;
		}
		return ProductSpecificationDTO.builder().fieldId(spec.getFieldid()).srNo(spec.getSrno())
				.fieldName(spec.getFieldname()).fieldValue(spec.getFieldvalue()).build();
	}

	public static List<ProductSpecificationDTO> toProductSpecificationDTOList(List<ProductSpecificationDetails> specs) {
		if (specs == null) {
			return null;
		}
		return specs.stream().map(ProductMapper::toProductSpecificationDTO).collect(Collectors.toList());
	}

	public static ProductSpecificationDetails toProductSpecificationEntity(ProductSpecificationDTO specDTO) {
		if (specDTO == null) {
			return null;
		}
		return ProductSpecificationDetails.builder().fieldid(specDTO.getFieldId()).srno(specDTO.getSrNo())
				.fieldname(specDTO.getFieldName()).fieldvalue(specDTO.getFieldValue()).build();
	}

	public static List<ProductSpecificationDetails> toProductSpecificationEntityList(
			List<ProductSpecificationDTO> specDTOs) {
		if (specDTOs == null) {
			return null;
		}
		return specDTOs.stream().map(ProductMapper::toProductSpecificationEntity).collect(Collectors.toList());
	}
}
