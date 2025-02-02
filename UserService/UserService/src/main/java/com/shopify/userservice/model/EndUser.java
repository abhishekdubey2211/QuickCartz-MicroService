package com.shopify.userservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
public class EndUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "Username is mandatory")
	@Size(max = 100, message = "Username must be less than 100 characters")
	private String username;

	@NotBlank(message = "Password is mandatory")
	private String password;

	@NotBlank(message = "Designation is mandatory")
	@Size(max = 100, message = "Designation must be less than 100 characters")
	private String designation;

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Invalid Email Address")
	@Size(max = 100, message = "Email must be less than 100 characters")
	@Column(unique = true)
	private String email;

	@NotBlank(message = "Contact number is mandatory")
	@Size(max = 100, message = "Contact must be less than 100 characters")
	@Pattern(regexp = "^\\+?[0-9]{1,3}?[-.\\s]?\\(?[0-9]{1,4}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}$", message = "Invalid contact number format")
	private String contact;

	@Size(max = 150, message = "Profileimage must be less than 150 characters")
	private String profileimage;

    @NotBlank(message = "User creation date is mandatory")
	@Size(max = 50, message = "User creation date must be less than 50 characters")
    private String usercreationdate; 

    @Past(message = "Date of birth must be in the past") 
    @Column(name = "date_of_birth")
    private LocalDate dateofbirth; 

	@Size(max = 50, message = "User  last updated date must be less than 50 characters")
    private String userlastupdatedate; 

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Set<Role> roles ;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<UserParameterDetails> userParameterDetails ;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<RessidentialAddress> address ;
	
    private int isactive;
    private int isdelete; 


}
