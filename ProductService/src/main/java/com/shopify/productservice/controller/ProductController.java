package com.shopify.productservice.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shopify.productservice.dto.ProductDto;
import com.shopify.productservice.dto.ProductRequestDTO;
import com.shopify.productservice.dto.ResponseApi;
import com.shopify.productservice.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/product") // Base path standardized for product-related APIs
@Slf4j
public class ProductController {

	@Autowired
	private ProductService productService;

	// Create a new product
	@PostMapping
	public ResponseEntity<ResponseApi> createProduct(@RequestBody ProductDto product) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseApi.createResponse(1, productService.addProduct(product)));
	}

	// Update an existing product
	@PutMapping
	public ResponseEntity<ResponseApi> updateProduct(@RequestBody ProductDto product) {
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(ResponseApi.createResponse(1, productService.editProduct(product)));
	}

	// Get all products (without pagination)
	@GetMapping
	public ResponseEntity<ResponseApi> getAllProducts() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getAllProducts()));
	}

	// Get paginated products
	@GetMapping("/paginated")
	public ResponseEntity<ResponseApi> getAllProductsPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getAllProducts(page, size)));
	}

	// Get all in-stock products
	@GetMapping("/in-stock")
	public ResponseEntity<ResponseApi> getAllInStockProducts() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getAllInStocksProduct()));
	}

	// Get all out-of-stock products
	@GetMapping("/out-of-stock")
	public ResponseEntity<ResponseApi> getAllOutOfStockProducts() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getAllOutOfStockProducts()));
	}

	// Get products by category
	@GetMapping("/category/{categoryName}")
	public ResponseEntity<ResponseApi> getProductsByCategory(@PathVariable("categoryName") String categoryName) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getProductByType(categoryName)));
	}

	// Get products by brand with pagination
	@GetMapping("/brand/{brandName}")
	public ResponseEntity<ResponseApi> getProductsByBrand(@PathVariable("brandName") String brandName,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getProductsByBrand(brandName, page, size)));
	}

	// Disable (soft delete) a product
	@DeleteMapping("/{productId}")
	public ResponseEntity<ResponseApi> disableProduct(@PathVariable("productId") Long productId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.disableProduct(productId)));
	}

	// Get product by ID
	@GetMapping("/{productId}")
	public ResponseEntity<ResponseApi> getProductById(@PathVariable("productId") Long productId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getProductById(productId)));
	}

	// Update product quantity
	@PutMapping("/{productId}/quantity/{quantity}")
	public ResponseEntity<ResponseApi> updateProductQuantity(@PathVariable("productId") Long productId,
			@PathVariable("quantity") int quantity) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.addProductQuantity(productId, quantity)));
	}

	// Get product summary
	@GetMapping("/summary")
	public ResponseEntity<ResponseApi> getProductSummary() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(ResponseApi.createResponse(1, productService.getProductSummary()));
	}

	// Filter and sort products dynamically
	@PostMapping("/add/filter")
	public ResponseEntity<ResponseApi> getFilteredAndSortedProducts(@RequestBody ProductRequestDTO requestDTO) {
		Map<String, Object> response = productService.getFilteredAndSortedProducts(requestDTO);
		return ResponseEntity.status(HttpStatus.OK).body(ResponseApi.createResponse(1, List.of(response)));
	}
}
