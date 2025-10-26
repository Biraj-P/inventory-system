package com.inventoryapp.inventory_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaleRequest {
    @NotBlank(message = "SKU is required")
    private String sku;

    @Min(value = 1, message = "Quantity sold must be at least 1")
    private Integer quantitySold;
}
