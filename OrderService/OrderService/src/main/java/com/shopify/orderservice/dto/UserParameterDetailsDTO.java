package com.shopify.orderservice.dto;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserParameterDetailsDTO {

    private int parameterId;
    private int srNo;
    private String value;
    private String description;

    public ErrorStatusDetails validateUserParameterDetailsDTO(UserParameterDetailsDTO userParameterDetailsDTO) {
    	
        // Validate value
        if (userParameterDetailsDTO.getValue() == null || userParameterDetailsDTO.getValue().trim().isEmpty()) {
            return new ErrorStatusDetails(4031, "Value is mandatory", HttpStatus.BAD_REQUEST);
        }
        
        if (userParameterDetailsDTO.getValue().length() > 255) {
            return new ErrorStatusDetails(4032, "Value cannot exceed 255 characters", HttpStatus.BAD_REQUEST);
        }

        // Validate description
        if (userParameterDetailsDTO.getDescription() != null && userParameterDetailsDTO.getDescription().length() > 500) {
            return new ErrorStatusDetails(4033, "Description cannot exceed 500 characters", HttpStatus.BAD_REQUEST);
        }

        // All validations passed
        return null;
    }
}
