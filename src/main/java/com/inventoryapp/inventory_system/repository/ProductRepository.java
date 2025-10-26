package com.inventoryapp.inventory_system.repository;

import com.inventoryapp.inventory_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //Marks this class as a repository component
//This class is used to access the database
//Extends JpaRepository<T, ID> to get basic CRUD methods for Product entity (using Long as ID type)
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Custom query method adhering to Spring Data JPA's conventions
    // Spring automatically generates the implementation for: SELECT * FROM product WHERE Sku = ?
    Product findBySku(String sku);
}
