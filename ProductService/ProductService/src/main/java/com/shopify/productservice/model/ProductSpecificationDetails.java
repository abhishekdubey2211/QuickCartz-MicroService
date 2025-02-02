package com.shopify.productservice.model;

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
@Table(name = "product_specifications",
       indexes = {
           @Index(name = "idx_fieldid", columnList = "fieldid"),
           @Index(name = "idx_srno", columnList = "srno"),
           @Index(name = "idx_fieldname", columnList = "fieldname")
       })

public class ProductSpecificationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @Column(name = "fieldid", nullable = false)
    @NotNull(message = "Field ID is mandatory")
    private int fieldid;

    @Column(name = "srno", nullable = false)
    @NotNull(message = "Serial number is mandatory")
    private int srno;

    @Column(name = "fieldvalue", nullable = false)
    @NotBlank(message = "Field value is mandatory")
    @Size(max = 255, message = "Field value must not exceed 255 characters")
    private String fieldvalue;

    @Column(name = "fieldname", nullable = false)
    @NotBlank(message = "Field name is mandatory")
    @Size(max = 255, message = "Field name must not exceed 255 characters")
    private String fieldname;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Product product;
}