package com.shopify.productservice.service.implementation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.shopify.productservice.dto.ErrorStatusDetails;
import com.shopify.productservice.dto.ProductDto;
import com.shopify.productservice.dto.ProductRequestDTO;
import com.shopify.productservice.exception.CustomException;
import com.shopify.productservice.implementation.MyEntitySpecification;
import com.shopify.productservice.mapper.ProductMapper;
import com.shopify.productservice.model.FilterCriteria;
import com.shopify.productservice.model.Product;
import com.shopify.productservice.repository.ProductRepository;
import com.shopify.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImplementation implements ProductService {
	ProductRepository productRepository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	public ProductServiceImplementation(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}

	String INSTOCK = "IN-STOCK";
	String OUTOFFSTOCK = "OUTOFF-STOCK";

	@Override
	public ProductDto addProduct(ProductDto pushProductDTO) {
		ErrorStatusDetails error = pushProductDTO.validateProductRequest(pushProductDTO, "add");
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		Product product = ProductMapper.toProductEntity(pushProductDTO);
		product.setActive(1);
		setProductStockStatus(product);
		product.setPrice(calculatePrice(product));
		product.setLast_refilled_date(LocalDateTime.now().format(formatter));
		product.getProductspecification().forEach(spec -> spec.setProduct(product));
		product.getImages().forEach(image -> image.setProduct(product));
		return ProductMapper.toProductDto(productRepository.save(product));
	}

	@Override
	public ProductDto editProduct(ProductDto updatedProductDTO) {
		ErrorStatusDetails error = updatedProductDTO.validateProductRequest(updatedProductDTO, "edit");
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		Product putProduct = ProductMapper.toProductEntity(updatedProductDTO);
		Product existingProduct = productRepository.findById(putProduct.getId())
				.orElseThrow(() -> new CustomException(5034, "BAD_REQUEST",
						"No product found with productId " + updatedProductDTO.getProductId()));
		existingProduct.setDiscount(putProduct.getDiscount());
		existingProduct.setName(putProduct.getName());
		existingProduct.setBrand(putProduct.getBrand());
		existingProduct.setCategory(putProduct.getCategory());
		existingProduct.setModel(putProduct.getModel());
		existingProduct.setMarketprice(putProduct.getMarketprice());
		existingProduct.setPrice(calculatePrice(existingProduct));
		existingProduct.setQuantity(putProduct.getQuantity());
		existingProduct.setDescription(putProduct.getDescription());
		setProductStockStatus(putProduct);
		existingProduct.getImages().clear();
		existingProduct.getProductspecification().clear();
		putProduct.getImages().forEach(image -> {
			image.setProduct(putProduct);
			existingProduct.getImages().add(image);
		});
		putProduct.getProductspecification().forEach(productspecification -> {
			productspecification.setProduct(putProduct);
			existingProduct.getProductspecification().add(productspecification);
		});
		existingProduct.setIsdelete(0);
		existingProduct.setActive(1);
		existingProduct.setLastupdateddate(LocalDateTime.now().format(formatter));
		existingProduct.setLast_refilled_date(LocalDateTime.now().format(formatter));
		return ProductMapper.toProductDto(productRepository.save(existingProduct));
	}

	@Override
	public ProductDto getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new CustomException(5034, "BAD_REQUEST", "No product found with productId " + id));
		if (product.getIsdelete() == 1) {
			throw new CustomException(5034, "BAD_REQUEST", "No product found with productId " + id);
		}
		return ProductMapper.toProductDto(product);
	}

	@Override
	public List<ProductDto> getProductByType(String type) {
		List<Product> products = productRepository.findAll();
		List<ProductDto> activeProducts = products.stream()
				.filter(product -> product.getCategory().equalsIgnoreCase(type) && product.getInstock() > 0
						&& product.getIsdelete() == 0)
				.map(product -> ProductMapper.toProductDto(product)).collect(Collectors.toList());
		return activeProducts;
	}

	@Override
	public ProductDto addProductQuantity(Long productid, int productQuantity) {
		Product existingProduct = productRepository.findById(productid).orElseThrow(
				() -> new CustomException(5034, "BAD_REQUEST", "No product found with productId " + productid));
		if (existingProduct.getIsdelete() != 0) {
			throw new CustomException(5035, "BAD_REQUEST", "No product found with productId " + productid);
		}
		existingProduct.setQuantity(existingProduct.getInstock() + productQuantity);
		existingProduct.setInstock(existingProduct.getQuantity());
		return ProductMapper.toProductDto(productRepository.save(existingProduct));
	}

	@Override
	public List<ProductDto> getAllProducts() {
		return productRepository.findAll().stream().filter(p -> p.getIsdelete() == 0 && p.getActive() == 1)
				.sorted(Comparator.comparing(Product::getId).reversed()).map(ProductMapper::toProductDto)
				.collect(Collectors.toList());
	}

	@Override
	public Page<ProductDto> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Product> productPage = productRepository.findAll(pageable);
		Page<ProductDto> productList = productPage.map(product -> {
			if (product.getIsdelete() == 0 && product.getActive() == 1) {
				return ProductMapper.toProductDto(product);
			}
			return null;
		});
		return productList;
	}

	@Override
	public List<ProductDto> getAllInStocksProduct() {
		List<Product> products = new ArrayList<>();
		products = productRepository.findAll().stream()
				.filter(p -> p.getIsdelete() == 0 || p.getProductstatus().equalsIgnoreCase(INSTOCK))
				.collect(Collectors.toList());
		List<ProductDto> productList = products.stream()
				.filter(p -> p.getIsdelete() == 0 && p.getActive() == 1 && p.getQuantity() > 0)
				.map(product -> ProductMapper.toProductDto(product)).collect(Collectors.toList());
		return productList;
	}

	@Override
	public List<ProductDto> getAllOutOfStockProducts() {
		List<ProductDto> productList = productRepository.findAll().stream().filter(
				p -> p.getQuantity() <= 0 && p.getIsdelete() == 0 && p.getProductstatus().equalsIgnoreCase(OUTOFFSTOCK))
				.map(product -> ProductMapper.toProductDto(product)).collect(Collectors.toList());
		return productList;
	}

	@Override
	public String disableProduct(Long id) {
		Product retrivedProduct = productRepository.findById(id)
				.orElseThrow(() -> new CustomException(5034, "BAD_REQUEST", "No product found with productId " + id));
		if (retrivedProduct.getIsdelete() == 1) {
			throw new CustomException(5034, "BAD_REQUEST", "No product found with productId " + id);
		}
		retrivedProduct.setActive(0);
		retrivedProduct.setIsdelete(1);
		retrivedProduct.setLastupdateddate(LocalDateTime.now().format(formatter));
		productRepository.save(retrivedProduct);
		return "Poduct with ProductId " + id + " is disable done Sucessfully......";
	}

	@Override
	public List<JSONObject> getProductSummary() {
		List<Product> products = productRepository.findAll();
		List<Product> productList = products.stream().filter(p -> p.getIsdelete() == 0 && p.getActive() == 1)
				.collect(Collectors.toList());
		List<JSONObject> productSummaryArray = new ArrayList<>();
		productList.forEach(product -> {
			JSONObject productSummary = new JSONObject();
			productSummary.put("productId", product.getId());
			productSummary.put("productName", product.getName());
			productSummary.put("brandName", product.getBrand());
			productSummary.put("stockQuantity", product.getInstock());
			productSummary.put("status", product.getProductstatus());
			productSummaryArray.add(productSummary);
		});
		return productSummaryArray;
	}

	

	@Override
	public Map<String, Object> getProductsByBrand(String strBrand, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "discount"));
		Page<Product> content = productRepository.findByBrand(strBrand, pageable);
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("content", content.getContent());

		response.put("pageNumber", content.getNumber());
		response.put("pageSize", content.getSize());
		response.put("totalItems", content.getTotalElements());
		response.put("totalPages", content.getTotalPages());
		Map<String, Object> sortDetails = new LinkedHashMap<>();
		sortDetails.put("sortedBy", "discount");
		sortDetails.put("sortDirection", "DESC");
		response.put("sort", sortDetails);
		return response;
	}

	
	private void setProductStockStatus(Product product) {
		product.setInstock(product.getQuantity());
		product.setProductstatus(product.getInstock() > 0 ? INSTOCK : OUTOFFSTOCK);
	}

	private double calculatePrice(Product putProduct) {
		return Math.round(
				(putProduct.getMarketprice() - (putProduct.getDiscount() * putProduct.getMarketprice()) / 100) * 100.0)
				/ 100.0;
	}
	

	@Override
    public Map<String, Object> getFilteredAndSortedProducts(ProductRequestDTO requestDTO) {
        int page = requestDTO.getPage();
        int size = requestDTO.getSize();
        String sortBy = requestDTO.getSortBy();
        String sortDir = requestDTO.getSortDir();
        List<FilterCriteria> filterCriteriaList = requestDTO.getFilterCriteriaList();

        Specification<Product> spec = MyEntitySpecification.getFilteredSpec(filterCriteriaList);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", productPage.getContent());
        response.put("pageNo", productPage.getNumber());
        response.put("pageSize", productPage.getSize());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("last", productPage.isLast());
        response.put("sortDetails",
                pageable.getSort().stream()
                        .map(order -> Map.of("property", order.getProperty(), "direction", order.getDirection().name()))
                        .collect(Collectors.toList()));
        response.put("filterCriteria", filterCriteriaList);

        return response;
    }
}
