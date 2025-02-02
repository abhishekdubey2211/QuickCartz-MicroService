package com.shopify.productservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "product", indexes = {
        @Index(name = "idx_product_brand", columnList = "brand_name"),
        @Index(name = "idx_product_category", columnList = "category_name"),
        @Index(name = "idx_product_active", columnList = "active")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    @Column(name = "brand_name", nullable = false)
    @NotBlank(message = "Brand name cannot be empty")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String brand;

    @Column(name = "category_name", nullable = false)
    @NotBlank(message = "Category cannot be empty")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Column(name = "model_detail", nullable = false)
    @NotBlank(message = "Model details cannot be empty")
    @Size(max = 150, message = "Model details must not exceed 150 characters")
    private String model;

    @Column(name = "discount", nullable = false)
    @Min(value = 0, message = "Discount must be at least 0")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private double discount;

    @Column(name = "marketprice", nullable = false)
    @Positive(message = "Market price must be positive")
    private double marketprice;

    @Column(name = "price", nullable = false)
    @Positive(message = "Price must be positive")
    private double price;

    @Column(name = "quantity", nullable = false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Description cannot be empty")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "productstatus", nullable = false)
    @NotBlank(message = "Product status cannot be empty")
    private String productstatus;

    @Column(name = "last_recieved_date", nullable = false)
    @NotBlank(message = "Last received date cannot be empty")
    private String last_refilled_date;

    @Column(name = "last_updated_date")
    @JsonIgnore
    private String lastupdateddate;

    @Column(name = "instock", nullable = false)
    @Min(value = 0, message = "In-stock count cannot be negative")
    private int instock;

    @Column(name = "active", nullable = false)
    @Min(value = 0, message = "Active status must be 0 or 1")
    @Max(value = 1, message = "Active status must be 0 or 1")
    private int active;

    @Column(name = "isdelete", nullable = false)
    @Min(value = 0, message = "Delete status must be 0 or 1")
    @Max(value = 1, message = "Delete status must be 0 or 1")
    private int isdelete;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImages> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductSpecificationDetails> productspecification;
}
