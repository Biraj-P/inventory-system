package com.inventoryapp.inventory_system.service;

import com.inventoryapp.inventory_system.dto.ProductRequest;
import com.inventoryapp.inventory_system.dto.SaleRequest;
import com.inventoryapp.inventory_system.model.Product;

import java.util.List;

public interface InventoryService {

    //CRUD: Add product using the DTO contract
    Product addProduct(ProductRequest productRequest);

    //CRUD: Retrieve all products
    List<Product> findAllProducts();

    //Business logic: The core sale transaction
    Product updateStockAfterSale(SaleRequest saleRequest);

    //Utility/Helper methods: Find product by SKU
    Product findBySku(String sku);
}
