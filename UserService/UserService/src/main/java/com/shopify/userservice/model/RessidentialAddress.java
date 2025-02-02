package com.shopify.userservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "address")
public class RessidentialAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Address line 1 is mandatory")
    @Size(max = 255, message = "Address line 1 must be less than 255 characters")
    @Column(name = "address1")
    private String address1;

    @Size(max = 255, message = "Address line 2 must be less than 255 characters")
    @Column(name = "address2")
    private String address2;

    @NotBlank(message = "State is mandatory")
    @Size(max = 100, message = "State must be less than 100 characters")
    @Column(name = "state")
    private String state;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City must be less than 100 characters")
    @Column(name = "city")
    private String city;

    @Size(max = 100, message = "Village name must be less than 100 characters")
    @Column(name = "village")
    private String village;

    @NotBlank(message = "Pincode is mandatory")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format") 
    @Column(name = "pincode")
    private String pincode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private EndUser user;

    private boolean isPrimary; 
}