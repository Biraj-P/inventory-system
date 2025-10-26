package com.inventoryapp.inventory_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //This annotation applies this class globally to all controllers
public class GlobalExceptionHandler {

    //1. Handle our custom business logic exceptions
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException e){
        //Return HTTP 400 with a custom message
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //2. Handle JPA/Resource not found errors
    @ExceptionHandler(ProductNotFoundException.class) //findBySku method throws this exception
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); //HTTP 404 NOT FOUND
    }

    //3. Handle validation errors (eg. when @Min or @NotBlank in DTO fails)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        //Collect all validation errors and return them as a single string(sends a detailed 400)
        String errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst().orElse("Validation Error.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
