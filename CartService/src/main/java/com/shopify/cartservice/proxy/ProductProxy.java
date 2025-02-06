package com.shopify.cartservice.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopify.cartservice.dto.ProductDto;
import com.shopify.cartservice.dto.ResponseApi;

@Component
@FeignClient(name = "PRODUCT-SERVICE", path = "/api/product")
public interface ProductProxy {

	// Create a new product
	@PostMapping
	public ResponseEntity<ResponseApi> createProduct(@RequestBody ProductDto product);

	// Update an existing product
	@PutMapping
	public ResponseEntity<ResponseApi> updateProduct(@RequestBody ProductDto product);

	// Get all products (without pagination)
	@GetMapping
	public ResponseEntity<ResponseApi> getAllProducts();

	// Get paginated products
	@GetMapping("/paginated")
	public ResponseEntity<ResponseApi> getAllProductsPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size);

	// Get all in-stock products
	@GetMapping("/in-stock")
	public ResponseEntity<ResponseApi> getAllInStockProducts();

	// Get all out-of-stock products
	@GetMapping("/out-of-stock")
	public ResponseEntity<?> getAllOutOfStockProducts();

	// Get products by category
	@GetMapping("/category/{categoryName}")
	public ResponseEntity<?> getProductsByCategory(@PathVariable("categoryName") String categoryName);

	// Get products by brand with pagination
	@GetMapping("/brand/{brandName}")
	public ResponseEntity<?> getProductsByBrand(@PathVariable("brandName") String brandName,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

	
	// Disable (soft delete) a product
	@DeleteMapping("/{productId}")
	public ResponseEntity<ResponseApi> disableProduct(@PathVariable("productId") Long productId);

	// Get product by ID
	@GetMapping("/{productId}")
	public ResponseEntity<ResponseApi> getProductById(@PathVariable("productId") Long productId);

	// Update product quantity
	@PutMapping("/{productId}/quantity/{quantity}")
	public ResponseEntity<ResponseApi> updateProductQuantity(@PathVariable("productId") Long productId,
			@PathVariable("quantity") int quantity);

	// Get product summary
	@GetMapping("/summary")
	public ResponseEntity<?> getProductSummary();

}
