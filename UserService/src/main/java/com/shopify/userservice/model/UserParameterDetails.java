package com.shopify.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserParameterDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long id;

	@NotNull(message = "Parameter ID is mandatory")
	@Positive(message = "Parameter ID must be a positive number")
	@Column(name = "parameterid")
	private int parameterid;

	@NotNull(message = "Serial number is mandatory")
	@Positive(message = "Serial number must be a positive number")
	@Column(name = "srno")
	private int srno;

	@NotBlank(message = "Value is mandatory")
	@Size(max = 255, message = "Value must be less than 255 characters")
	@Column(name = "value")
	private String value;

	@Size(max = 500, message = "Description must be less than 500 characters")
	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@JsonIgnore
	private EndUser user;

	public UserParameterDetails(
			@NotNull(message = "Parameter ID is mandatory") @Positive(message = "Parameter ID must be a positive number") int parameterid,
			@NotNull(message = "Serial number is mandatory") @Positive(message = "Serial number must be a positive number") int srno,
			@NotBlank(message = "Value is mandatory") @Size(max = 255, message = "Value must be less than 255 characters") String value,
			@Size(max = 500, message = "Description must be less than 500 characters") String description,
			EndUser user) {
		super();
		this.parameterid = parameterid;
		this.srno = srno;
		this.value = value;
		this.description = description;
		this.user = user;
	}
	
	
}