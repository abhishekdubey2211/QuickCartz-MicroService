package com.shopify.orderservice.service.implementation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopify.orderservice.dto.EndUserDTO;
import com.shopify.orderservice.dto.ErrorStatusDetails;
import com.shopify.orderservice.dto.OrderDto;
import com.shopify.orderservice.dto.ProductDto;
import com.shopify.orderservice.dto.ResponseApi;
import com.shopify.orderservice.exception.CustomException;
import com.shopify.orderservice.mapper.OrderMapper;
import com.shopify.orderservice.model.Order;
import com.shopify.orderservice.model.OrderDetails;
import com.shopify.orderservice.proxy.ProductProxy;
import com.shopify.orderservice.proxy.UserProxy;
import com.shopify.orderservice.repository.OrderRepository;
import com.shopify.orderservice.service.OrderService;
import com.shopify.orderservice.utils.GsonConfig;

import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImplementation implements OrderService {
	public static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	OrderMapper mapper = new OrderMapper();

	@Autowired
	ProductProxy productProxy;

	@Autowired
	UserProxy userProxy;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	OrderRepository orderRepository;
	private final ObjectMapper objectMapper = new ObjectMapper(); // Ensure ObjectMapper is initialized

	@Transactional
	public OrderDto placeOrder(Long userid, OrderDto pushOrderDto) {
		ErrorStatusDetails error = pushOrderDto.validateOrderRequest(pushOrderDto);
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}
		Order order = OrderMapper.toOrderEntity(pushOrderDto);
		EndUserDTO endUserDTO = fetchUser(userid);
		if (endUserDTO == null) {
			throw new CustomException(7034, "BAD_REQUEST", "No User Found with UserId " + userid);
		}

		order.setUser(userid);
		order.setDate(LocalDateTime.now().format(formatter));
		order.setIsdelevered("NO");
		order.setStatus("ORDER PLACED");

		List<OrderDetails> orderDetails = pushOrderDto.getOrderDetails().stream().map(OrderMapper::toOrderDetailEntity)
				.collect(Collectors.toList());

		orderDetails.forEach(detail -> {
			detail.setProductorderstatus("Not Delivered");
			detail.setOrder(order);
		});

		List<OrderDetails> updatedOrderItem = new ArrayList<>();
		for (OrderDetails orderItem : orderDetails) {
			boolean productExists = false;
			for (OrderDetails existingItem : updatedOrderItem) {
				if (existingItem.getProduct() == orderItem.getProduct()) {
					existingItem.setQuantity(existingItem.getQuantity() + orderItem.getQuantity());
					productExists = true;
					break;
				}
			}
			if (!productExists) {
				updatedOrderItem.add(orderItem);
			}
		}

		Double totalOrderPrice = updatedOrderItem.stream().map(this::calculateBucketItemPrice).reduce(0.0, Double::sum);

		order.setTotalamount(totalOrderPrice);
		order.setOrderdetails(updatedOrderItem);
		order.setIspaymentdone(pushOrderDto.getIsPaymentDone());
		order.setPaymentmode(pushOrderDto.getPaymentMode());
		order.getOrderdetails().forEach(bucketItem -> bucketItem.setOrder(order));
		Order savedOrder = orderRepository.save(order);
		return OrderMapper.toOrderDto(savedOrder);
	}

	public Double calculateBucketItemPrice(OrderDetails bucketItem) {
		ProductDto product = fetchProduct(bucketItem.getProduct());
		validateProductAvailability(product, bucketItem);
		double itemPrice = product.getPrice() * bucketItem.getQuantity();
		bucketItem.setPrice(itemPrice);
		bucketItem.setProductname(product.getProductName());
		bucketItem.setProduct(product.getProductId());
		product.setProductQuantity(product.getProductQuantity() - bucketItem.getQuantity());
		productProxy.updateProduct(product);
		bucketItem.setProductorderstatus("ORDER_ITEM_PLACED");
		return itemPrice;
	}

	private ProductDto fetchProduct(Long productId) {
		try {
			ResponseEntity<ResponseApi> responseEntity = productProxy.getProductById(productId);
			ResponseApi response = objectMapper.convertValue(responseEntity.getBody(), ResponseApi.class);
			List<ProductDto> productList = objectMapper.convertValue(response.getData(),
					new TypeReference<List<ProductDto>>() {
					});
			if (productList == null || productList.isEmpty()) {
				throw new CustomException(404, "BAD_REQUEST", "No product found with productId " + productId);
			}
			return productList.get(0);
		} catch (FeignException e) {
			handleFeignException(e,"PRODUCT-SERVICE");
		} catch (Exception e) {
			throw new CustomException(500, "BAD_REQUEST", "Failed to retrieve product details");
		}
		return null;
	}

	private void validateProductAvailability(ProductDto product, OrderDetails bucketItem) {
		if ("OUTOFF-STOCK".equals(product.getProductStatus())) {
			throw new CustomException(7002, "BAD_REQUEST", "Product " + product.getProductName() + " is OUT OF STOCK");
		}
		if (product.getProductQuantity() < bucketItem.getQuantity()) {
			throw new CustomException(7004, "BAD_REQUEST", "For " + product.getProductName() + ", you can order only "
					+ product.getProductQuantity() + " items");
		}
	}

	private void handleFeignException(FeignException e, String serviceName) {
		String errorMessage = e.contentUTF8();
		if (errorMessage == null || errorMessage.isEmpty()) {
			log.info("handleFeignException :: " + errorMessage +e);
			throw new CustomException(500, "BAD_REQUEST", serviceName + " IS DOWN");
		}
		ResponseApi response = new Gson().fromJson(errorMessage, ResponseApi.class);
		throw new CustomException(response.getStatus(), "BAD_REQUEST", response.getMessage());
	}

	@Override
	public OrderDto getPlacedOrderDetailsByOrderId(Long orderid) {
		Optional<Order> retrivedOrder = orderRepository.findById(orderid);
		if (retrivedOrder.isEmpty()) {
			throw new CustomException(7031, "BAD_REQUEST", "No OrderDetails found with OrderId " + orderid);
		}
		Order orderdetails = retrivedOrder.get();
		OrderDto dto = OrderMapper.toOrderDto(orderdetails);
		orderdetails.getOrderdetails().stream().forEach(bucket -> {
			long productid = bucket.getProduct();
			dto.getOrderDetails().forEach(dtoBucket -> {
				if (bucket.getId() == dtoBucket.getOrderDetailId()) {
					dtoBucket.setProductId(productid);
				}
			});
		});
		return dto;
	}

	@Override
	public List<OrderDto> getOrdersByUserId(Long userid) {
		EndUserDTO endUserDTO = fetchUser(userid);
		if (endUserDTO == null) {
			throw new CustomException(7034, "BAD_REQUEST", "No User Found with UserId " + userid);
		}
		List<Order> orders = orderRepository.findByUser(userid)
				.orElseThrow(() -> new CustomException(7035, "BAD_REQUEST", "No Order Found with userid " + userid));
		return orders.stream().map(order -> OrderMapper.toOrderDto(order)).toList();
	}

	@Override
	public OrderDto cancelOrder(Long orderId) {

		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> new CustomException(7021, "BAD_REQUEST", "Order with OrderId " + orderId + " does not exists"));

		if (order.getStatus().equals("ORDER_CANCELLED")) {
			throw new CustomException(7022, "BAD_REQUEST", "Order is already cancelled with OrderId " + orderId);
		}
		if (order.getStatus().equals("ORDER_DELIVERED")) {
			throw new CustomException(7023, "BAD_REQUEST", "Delivered orders cannot be cancelled.");
		}

		order.setStatus("ORDER_CANCELLED");

		for (OrderDetails orderDetail : order.getOrderdetails()) {
			ProductDto product = fetchProduct(orderDetail.getProduct());
			int updatedQuantity = product.getProductQuantity() + orderDetail.getQuantity();
			if (!orderDetail.getProductorderstatus().equals("ORDER_ITEM_CANCELLED")) {
				product.setProductQuantity(updatedQuantity);
			}
			orderDetail.setProductorderstatus("ORDER_ITEM_CANCELLED");
			productProxy.updateProduct(product);
		}

		order.setTotalamount(0);
		Order savedOrder = orderRepository.save(order);
		OrderDto dto = OrderMapper.toOrderDto(savedOrder);
		savedOrder.getOrderdetails().stream().forEach(bucket -> {
			long productid = bucket.getProduct();
			dto.getOrderDetails().forEach(dtoBucket -> {
				if (bucket.getId() == dtoBucket.getProductId()) {
					dtoBucket.setProductId(productid);
				}
			});
		});
		return dto;
	}

	@Override
	public OrderDto cancelOrderItem(Long orderId, Long productId) {

		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> new CustomException(7021, "BAD_REQUEST", "Order with OrderId " + orderId + " does not exists"));

		OrderDetails bucketItem = order.getOrderdetails().stream().filter(item -> item.getProduct() == productId)
				.findFirst().orElseThrow(() -> new CustomException(7021, "BAD_REQUEST",
						"Item not found in the order with productId " + productId));

		if (bucketItem.getProductorderstatus().equals("ORDER_ITEM_CANCELLED")) {
			throw new CustomException(7032, "BAD_REQUEST", "Item is already cancelled with productId " + productId);
		}

		bucketItem.setProductorderstatus("ORDER_ITEM_CANCELLED");
		ProductDto product = fetchProduct(productId);
		int updatedQuantity = product.getProductQuantity() + bucketItem.getQuantity();
		product.setProductQuantity(updatedQuantity);
		bucketItem.setProductorderstatus("ORDER_ITEM_CANCELLED");
		productProxy.updateProduct(product);

		if (order.getOrderdetails().size() == 1 && bucketItem.getProductorderstatus().equals("ORDER_ITEM_CANCELLED")) {
			order.setStatus("ORDER_CANCELLED");
		}

		order.setTotalamount(order.getTotalamount() - (bucketItem.getQuantity() * product.getPrice()));
		Order savedOrder = orderRepository.save(order);
		OrderDto dto = OrderMapper.toOrderDto(savedOrder);
		bucketItem.setQuantity(0);

		savedOrder.getOrderdetails().stream().forEach(orderDetail -> {
			long productid = orderDetail.getProduct();
			dto.getOrderDetails().forEach(dtoBucket -> {
				if (orderDetail.getProduct() == dtoBucket.getProductId()) {
					dtoBucket.setProductId(productId);
				}
			});
		});
		return dto;
	}

	private EndUserDTO fetchUser(Long userId) {
		try {
			ResponseEntity<ResponseApi> responseEntity = userProxy.getUserById(userId);
			ResponseApi response = objectMapper.convertValue(responseEntity.getBody(), ResponseApi.class);
			 // Create Gson instance
	        Gson gson = GsonConfig.createGson();

	        // Deserialize response data
	        List<EndUserDTO> userList = gson.fromJson(gson.toJson(response.getData()), 
	                new TypeToken<List<EndUserDTO>>(){}.getType());

			if (userList == null || userList.isEmpty()) {
				throw new CustomException(404, "BAD_REQUEST", "No product found with userId " + userId);
			}
			return userList.get(0);
		} catch (FeignException e) {
			handleFeignException(e, "USER-SERVICE");
		} catch (Exception e) {
			throw new CustomException(500, "BAD_REQUEST", "Failed to retrieve userId details :: "+e);
		}
		return null;
	}
}
