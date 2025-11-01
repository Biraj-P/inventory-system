package com.inventoryapp.inventory_system.service;

import com.inventoryapp.inventory_system.dto.ProductRequest;
import com.inventoryapp.inventory_system.dto.SaleRequest;
import com.inventoryapp.inventory_system.exception.InsufficientStockException;
import com.inventoryapp.inventory_system.exception.ProductNotFoundException;
import com.inventoryapp.inventory_system.model.Product;
import com.inventoryapp.inventory_system.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //Marks this class as a business service component
public class InventoryServiceImpl implements InventoryService{
    // DIP: Depend on the abstraction (ProductRepository)
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    //Industry standard: Constructor Injection (preferred over @Autowired fields)
    public InventoryServiceImpl(ProductRepository productRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.productRepository = productRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Product addProduct(ProductRequest productRequest){
        //Map DTO to Entity (Encapsulation)
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setSku(productRequest.getSku());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());

        return productRepository.save(product);
    }

    @Override
    public List<Product> findAllProducts() {
        // Simple call to the persistence layer (SRP)
        return productRepository.findAll();
    }

    @Override
    @Transactional //Essential: ensures all database operations succeed or fail together (ACID)
    public Product updateStockAfterSale(SaleRequest saleRequest){
        //1. Find the product
        Product product = productRepository.findBySku(saleRequest.getSku());

        //2. Business Rule: Check for sufficient stock

//        if(product == null){
//            //Best practice is to throw an exception (eg. ProductNotFoundException)
//            throw new RuntimeException("Insufficient stock. Only " + product.getStockQuantity() + " left.");
//        }
// BREAKS SRP

        if(product.getStockQuantity() < saleRequest.getQuantitySold()){
            //Throw the specific business exception
            throw new InsufficientStockException("Insufficient stock for SKU " + saleRequest.getSku() +
                    " . Only " + product.getStockQuantity() + " left.");
        }

        if(product == null){
            throw new ProductNotFoundException("Product not found with SKU: " + saleRequest.getSku());
        }

        //3. Update the stock quantity
        product.setStockQuantity(product.getStockQuantity() - saleRequest.getQuantitySold());

        //4. Save the updated product to the database
        Product updatedProduct = productRepository.save(product);

        //5. Notify clients about the stock update via WebSocket
        // Broadcast the updated product to all clients subscribed to the /topic/inventory-updates topic
        simpMessagingTemplate.convertAndSend("/topic/inventory-updates", updatedProduct);
        return updatedProduct;
    }

    @Override
    public Product findBySku(String sku){
        return productRepository.findBySku(sku);
    }
}
