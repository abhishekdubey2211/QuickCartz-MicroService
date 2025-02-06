package com.shopify.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "order_details")
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(name = "price", nullable = false)
	@PositiveOrZero(message = "Price must be zero or a positive number")
	private double price;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@JsonIgnore
	private Order order;

	@Column(name = "product_id", nullable = false)
	@Positive(message = "Product ID must be a positive number")
	private long product;

	@Column(name = "product_name", nullable = false)
	private String productname;
	
	@Column(name = "quantity", nullable = false)
	@Positive(message = "Quantity must be a positive number")
	private int quantity;

	@Column(name = "productorderstatus", nullable = false)
	@NotBlank(message = "Product order status cannot be blank")
	private String productorderstatus;
}
