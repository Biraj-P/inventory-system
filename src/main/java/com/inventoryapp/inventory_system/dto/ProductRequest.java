package com.inventoryapp.inventory_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data //Lombok: Generates getter, setter, toString, equals, hashCode
public class ProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
}
