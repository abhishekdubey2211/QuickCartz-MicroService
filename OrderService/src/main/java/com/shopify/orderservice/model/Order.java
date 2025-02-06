package com.shopify.orderservice.model;

import java.util.List;

import com.shopify.orderservice.dto.OrderDetailsDto;
import com.shopify.orderservice.dto.OrderDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "user_order", indexes = { 
		@Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_order_date", columnList = "order_date"),
//		@Index(name = "idx_order_userid", columnList = "order_userid"),
		@Index(name = "idx_status", columnList = "status")}
)
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
    
	@Column(name = "user_id", nullable = false)
	@Positive(message = "User ID must be a positive number")
	private long user;

	@Column(name = "order_date", nullable = false)
	@NotBlank(message = "Order date cannot be blank")
	private String date;

	@Column(name = "total_amount", nullable = false)
	@PositiveOrZero(message = "Total amount must be zero or positive")
	private double totalamount;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotEmpty(message = "OrderDetails cannot be empty")
	private List<OrderDetails> orderdetails;

	@Column(name = "paymentmode", nullable = false)
	@NotBlank(message = "Payment mode cannot be blank")
	private String paymentmode;

	@Column(name = "ispaymentdone", nullable = false)
	@Min(value = 0, message = "isPaymentDone must be 0 (false) or 1 (true)")
	@Max(value = 1, message = "isPaymentDone must be 0 (false) or 1 (true)")
	private int ispaymentdone;

	@Column(name = "status", nullable = false)
	@NotBlank(message = "Status cannot be blank")
	private String status;

	@Column(name = "isdelevered", nullable = false)
	@Pattern(regexp = "^(YES|NO)$", message = "isDelivered must be either 'YES' or 'NO'")
	private String isdelevered;
}
