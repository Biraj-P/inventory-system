package com.inventoryapp.inventory_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //Marks this class as an entity(table)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id //Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku; //Stock Keeping Unit (Unique ID)
    private String name;
    private Integer stockQuantity; // The core inventory value
    private Double price;
}
