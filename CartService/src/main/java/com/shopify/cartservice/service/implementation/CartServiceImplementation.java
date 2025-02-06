package com.shopify.cartservice.service.implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopify.cartservice.dto.CartDto;
import com.shopify.cartservice.dto.CartItemDto;
import com.shopify.cartservice.dto.EndUserDto;
import com.shopify.cartservice.dto.ErrorStatusDetails;
import com.shopify.cartservice.dto.ProductDto;
import com.shopify.cartservice.dto.ResponseApi;
import com.shopify.cartservice.exception.CustomException;
import com.shopify.cartservice.mapper.CartMapper;
import com.shopify.cartservice.repository.CartRepository;
import com.shopify.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shopify.cartservice.model.Cart;
import com.shopify.cartservice.model.CartItem;
import com.shopify.cartservice.proxy.ProductProxy;
import com.shopify.cartservice.proxy.UserProxy;
import com.shopify.cartservice.utils.GsonConfig;
import feign.FeignException;
import static java.lang.Math.log;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartServiceImplementation implements CartService {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartMapper mapper;

	@Autowired
	UserProxy userProxy;

	@Autowired
	ProductProxy productProxy;

	private final ObjectMapper objectMapper = new ObjectMapper(); // Ensure ObjectMapper is initialized

	@Override
	public CartDto createCartForUser(long UserId) {
		if (0 > UserId) {
			throw new CustomException(9001, "BAD_REQUEST", "Userid invalid");
		}
		
		EndUserDto user = fetchUser(UserId);
		
		Cart cart = Cart.builder().totalamount(0.0).userid(UserId).cartitem(new ArrayList<CartItem>()).build();
		Cart savedCart = cartRepository.save(cart);
		return CartMapper.toCartDto(savedCart);
	}

	private EndUserDto fetchUser(Long userId) {
		try {
			ResponseEntity<ResponseApi> responseEntity = userProxy.getUserById(userId);
			ResponseApi response = objectMapper.convertValue(responseEntity.getBody(), ResponseApi.class);
			// Create Gson instance
			Gson gson = GsonConfig.createGson();

			// Deserialize response data
			List<EndUserDto> userList = gson.fromJson(gson.toJson(response.getData()),
					new TypeToken<List<EndUserDto>>() {
					}.getType());

			if (userList == null || userList.isEmpty()) {
				throw new CustomException(404, "BAD_REQUEST", "No product found with userId " + userId);
			}
			return userList.get(0);
		} catch (FeignException e) {
			handleFeignException(e, "USER-SERVICE");
		} catch (Exception e) {
			throw new CustomException(500, "BAD_REQUEST", "Failed to retrieve userId details :: " + e);
		}
		return null;
	}

	private void handleFeignException(FeignException e, String serviceName) {
		String errorMessage = e.contentUTF8();
		if (errorMessage == null || errorMessage.isEmpty()) {
			log.info("handleFeignException :: " + errorMessage + e);
			throw new CustomException(500, "BAD_REQUEST", serviceName + " IS DOWN");
		}
		ResponseApi response = new Gson().fromJson(errorMessage, ResponseApi.class);
		throw new CustomException(response.getStatus(), "BAD_REQUEST", response.getMessage());
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
			handleFeignException(e, "PRODUCT-SERVICE");
		} catch (Exception e) {
			throw new CustomException(500, "BAD_REQUEST", "Failed to retrieve product details");
		}
		return null;
	}

	@Override
	public CartDto addProductToCart(Long userid, CartItemDto pushCartItemDTO) {
		ErrorStatusDetails error = pushCartItemDTO.validateCartItemRequest(pushCartItemDTO);
		if (error != null) {
			throw new CustomException(error.status(), "BAD_REQUEST", error.statusdescription());
		}

		// getUserDeatils by Id by fignclient
//		EndUserDto retrivedUser = fetchUser(userid);
		Cart userCart = cartRepository.findByUserid(userid)
			    .orElseThrow(() -> new CustomException(9021, "BAD_REQUEST", 
			        "No cart Found Associated to user with userid " + userid));

		ProductDto product = fetchProduct(pushCartItemDTO.getProductId());
		Optional<CartItem> existingCartItem = userCart.getCartitem().stream()
				.filter(item -> item.getProductid() == product.getProductId()).findFirst();
		CartItem cartItem;
		if (existingCartItem.isPresent()) {
			// Update the existing cart item
			cartItem = existingCartItem.get();
			int newQuantity = cartItem.getQuantity() + pushCartItemDTO.getProductQuantity();
			cartItem.setQuantity(newQuantity);
			cartItem.setPrice(cartItem.getPrice() + (product.getPrice() * pushCartItemDTO.getProductQuantity()));
		} else {
			// Create a new cart item if not already in the cart
			cartItem = new CartItem();
			cartItem.setProductid(product.getProductId());
			cartItem.setQuantity(pushCartItemDTO.getProductQuantity());
			cartItem.setPrice(product.getPrice() * pushCartItemDTO.getProductQuantity());
			cartItem.setCart(userCart);
			userCart.getCartitem().add(cartItem); // Add to the cart's list of items
		}

		double newTotalAmount = userCart.getCartitem().stream().mapToDouble(CartItem::getPrice).sum();
		userCart.setTotalamount(newTotalAmount);

		Cart savedCart = cartRepository.save(userCart);
		log.info("Product added to cart for user {}", userid);

		return CartMapper.toCartDto(savedCart);
	}

	@Override
	public CartDto removeItemFromCart(Long userid, Long productid) {
		// Retrieve the user
		Cart userCart = cartRepository.findByUserid(userid)
			    .orElseThrow(() -> new CustomException(9021, "BAD_REQUEST", 
			        "No cart Found Associated to user with userid " + userid));


        ProductDto fetchedProduct = fetchProduct(productid);

        Optional<CartItem> existingCartItem = userCart.getCartitem().stream()
                .filter(item -> item.getProductid() == fetchedProduct.getProductId()).findFirst();

        if (existingCartItem.isEmpty()) {
            throw new CustomException(9022,"BAD_REQUEST","No such product exists in the cart");
        }

        CartItem cartItemToRemove = existingCartItem.get();
        cartItemToRemove.setCart(null);
        userCart.getCartitem().remove(cartItemToRemove);

        double newTotalAmount = userCart.getCartitem().stream().mapToDouble(CartItem::getPrice).sum();
        userCart.setTotalamount(newTotalAmount);
        return CartMapper.toCartDto(cartRepository.save(userCart));
	}

	@Override
	public CartDto removeAllProducts(Long userid) {
		Cart userCart = cartRepository.findByUserid(userid)
			    .orElseThrow(() -> new CustomException(9021, "BAD_REQUEST", 
			        "No cart Found Associated to user with userid " + userid));

        List<CartItem> cartItems = userCart.getCartitem();
        if (cartItems.isEmpty()) {
            throw new CustomException(9021,"BAD_REQUEST",  "No products found in the cart to remove");
        }
        userCart.getCartitem().forEach(item -> item.setCart(null));
//        userCart.setUser(null);
        userCart.getCartitem().clear();
        userCart.setTotalamount(0.0);
        Cart savedCart = cartRepository.save(userCart);
        return CartMapper.toCartDto(savedCart);
	}

	@Override
	public List<CartItemDto> getAllCartItems(Long userid) {
		EndUserDto retrievedUser = fetchUser(userid);

		Cart userCart = (retrievedUser.getUserCart() != null) ? CartMapper.toCart(retrievedUser.getUserCart())
				: new Cart();

		if (userCart.getCartitem() == null) {
			userCart.setCartitem(List.of());
		}

		retrievedUser.setUserCart(CartMapper.toCartDto(userCart));

		List<CartItemDto> data = userCart.getCartitem().stream().map(item -> CartMapper.toCartItemDto(item))
				.collect(Collectors.toList());

		return data;
	}

}
