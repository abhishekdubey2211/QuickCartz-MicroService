package com.shopify.cartservice.dto;
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
}
