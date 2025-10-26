package com.inventoryapp.inventory_system.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message) {
        super(message);
    }
}
