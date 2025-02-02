package com.shopify.productservice.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;

import com.shopify.productservice.dto.ProductDto;
import com.shopify.productservice.dto.ProductRequestDTO;

public interface ProductService {
	ProductDto addProduct(ProductDto pushProductDTO);

	ProductDto editProduct(ProductDto updatedProductDTO);

	ProductDto getProductById(Long id);

	List<ProductDto> getProductByType(String type);

	ProductDto addProductQuantity(Long productid, int productQuantity);

	List<ProductDto> getAllProducts();

	Page<ProductDto> getAllProducts(int page, int size);

	List<ProductDto> getAllInStocksProduct();

	Map<String, Object> getFilteredAndSortedProducts(ProductRequestDTO requestDTO);

	List<ProductDto> getAllOutOfStockProducts();

	String disableProduct(Long id);

	List<JSONObject> getProductSummary();

	Map<String, Object> getProductsByBrand(String strBrand, int page, int size);
}
