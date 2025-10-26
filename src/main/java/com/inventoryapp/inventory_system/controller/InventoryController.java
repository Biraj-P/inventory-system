package com.inventoryapp.inventory_system.controller;

import com.inventoryapp.inventory_system.dto.ProductRequest;
import com.inventoryapp.inventory_system.dto.SaleRequest;
import com.inventoryapp.inventory_system.model.Product;
import com.inventoryapp.inventory_system.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    //1. Dependency Injection (DIP): The controller depends on the Interface (InventoryService)
    private final InventoryService inventoryService;

    //Constructor Injection (preferred over @Autowired fields)
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    //CREATE: POST /api/inventory
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest productRequest){
        Product newProduct = inventoryService.addProduct(productRequest);
        //HTTP 201 CREATED is the standard response for successful creation(POST requests)
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    //READ: GET /api/inventory
    @GetMapping
    public List<Product> getAllProducts(){
        return inventoryService.findAllProducts();
    }

    //Business Logic: POST /api/inventory/sale
    @PostMapping("/sale")
    public ResponseEntity<?> recordSale(@Valid @RequestBody SaleRequest saleRequest){
        try{
            Product updatedProduct = inventoryService.updateStockAfterSale(saleRequest);
            //returns the updated product status
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            //Handle business exceptions (like Insufficient stock) with a BAD REQUEST (400)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
