package com.shopify.cartservice.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EndUserDto {
    private long userId;
    private String userName;
    private String password;
    private String confirmPassword;
    private String designation;
    private String emailAddress;
    private String contactNumber;
    private String profileImage;
    private LocalDate dateOfBirth;
    private List<RessidentialAddressDTO> residentialAddress;
    private List<UserParameterDetailsDTO> userParameterDetails;
    private Set<Role> roles = new HashSet<>();
    private CartDto userCart;

}
