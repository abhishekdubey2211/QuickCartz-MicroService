package com.shopify.userservice.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import com.shopify.userservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EndUserDTO {
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
//    private CartDto userCart;

    public ErrorStatusDetails validateEndUserRequest(EndUserDTO endUserDTO, String operation) {
        // Validate username
        if (endUserDTO.getUserName() == null || endUserDTO.getUserName().trim().isEmpty()) {
            return new ErrorStatusDetails(4001, "Username is mandatory", HttpStatus.BAD_REQUEST);
        } else if (endUserDTO.getUserName().length() > 100) {
            return new ErrorStatusDetails(4002, "Username must not exceed 100 characters", HttpStatus.BAD_REQUEST);
        }

        // Validate designation
        if (endUserDTO.getDesignation() == null || endUserDTO.getDesignation().trim().isEmpty()) {
            return new ErrorStatusDetails(4003, "Designation is mandatory", HttpStatus.BAD_REQUEST);
        } else if (endUserDTO.getDesignation().length() > 100) {
            return new ErrorStatusDetails(4004, "Designation must not exceed 100 characters", HttpStatus.BAD_REQUEST);
        }

        // Validate email
        if (endUserDTO.getEmailAddress() == null || endUserDTO.getEmailAddress().trim().isEmpty()) {
            return new ErrorStatusDetails(4005, "Email is mandatory", HttpStatus.BAD_REQUEST);
        } else if (!endUserDTO.getEmailAddress().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return new ErrorStatusDetails(4006, "Invalid email format", HttpStatus.BAD_REQUEST);
        } else if (endUserDTO.getEmailAddress().length() > 100) {
            return new ErrorStatusDetails(4007, "Email must not exceed 100 characters", HttpStatus.BAD_REQUEST);
        }

        // Validate contact number
        if (endUserDTO.getContactNumber() == null || endUserDTO.getContactNumber().trim().isEmpty()) {
            return new ErrorStatusDetails(4008, "Contact number is mandatory", HttpStatus.BAD_REQUEST);
        } else {
            String contactPattern = "^\\+?[0-9]{1,3}?[-.\\s]?\\(?[0-9]{1,4}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}$";
            if (!endUserDTO.getContactNumber().matches(contactPattern)) {
                return new ErrorStatusDetails(4009, "Invalid contact number format", HttpStatus.BAD_REQUEST);
            } else if (endUserDTO.getContactNumber().length() > 15) {
                return new ErrorStatusDetails(4010, "Contact number must not exceed 15 digits", HttpStatus.BAD_REQUEST);
            }
        }

     // Validate date of birth
        if (endUserDTO.getDateOfBirth() == null) {
            return new ErrorStatusDetails(4011, "Date of birth is mandatory", HttpStatus.BAD_REQUEST);
        } else if (endUserDTO.getDateOfBirth().isAfter(LocalDate.now())) {
            return new ErrorStatusDetails(4012, "Date of birth cannot be in the future", HttpStatus.BAD_REQUEST);
        }

        // Validate profile image
        if (endUserDTO.getProfileImage() == null || endUserDTO.getProfileImage().trim().isEmpty()) {
            return new ErrorStatusDetails(4012, "Profile image is mandatory", HttpStatus.BAD_REQUEST);
        } else if (endUserDTO.getProfileImage().length() > 255) {
            return new ErrorStatusDetails(4013, "Profile image URL must not exceed 255 characters", HttpStatus.BAD_REQUEST);
        }

        // Validate residential address
        if (endUserDTO.getResidentialAddress() == null || endUserDTO.getResidentialAddress().isEmpty()) {
            return new ErrorStatusDetails(4014, "Residential address is mandatory", HttpStatus.BAD_REQUEST);
        }

        // Validate roles
        if (endUserDTO.getRoles() == null || endUserDTO.getRoles().isEmpty()) {
            return new ErrorStatusDetails(4015, "At least one role is required", HttpStatus.BAD_REQUEST);
        }

        // Operation-specific validations
        if (operation.equalsIgnoreCase("add")) {
            // Validate password
            if (endUserDTO.getPassword() == null || endUserDTO.getPassword().trim().isEmpty()) {
                return new ErrorStatusDetails(4016, "Password is mandatory", HttpStatus.BAD_REQUEST);
            } else if (endUserDTO.getPassword().length() < 8 || endUserDTO.getPassword().length() > 30) {
                return new ErrorStatusDetails(4017, "Password must be between 8 and 30 characters", HttpStatus.BAD_REQUEST);
            }

            // Validate confirm password
            if (endUserDTO.getConfirmPassword() == null || endUserDTO.getConfirmPassword().trim().isEmpty()) {
                return new ErrorStatusDetails(4018, "Confirm password is mandatory", HttpStatus.BAD_REQUEST);
            } else if (!endUserDTO.getPassword().equals(endUserDTO.getConfirmPassword())) {
                return new ErrorStatusDetails(4019, "Password and Confirm Password must match", HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

}
