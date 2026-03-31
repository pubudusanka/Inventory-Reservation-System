package com.order.management.inventoryreservation.controller;

import com.order.management.inventoryreservation.dto.OrderDto;
import com.order.management.inventoryreservation.dto.ProductDto;
import com.order.management.inventoryreservation.service.InventoryService;
import com.order.management.inventoryreservation.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // save product
    @PostMapping("/products")
    public ResponseEntity<StandardResponseDto> createProduct(@RequestBody ProductDto data){
        inventoryService.saveProduct(data);
        return new ResponseEntity<>(
                new StandardResponseDto(
                    201, "Product Create Successfully!", null
                ),
                HttpStatus.CREATED
        );
    }

    // generate order
    @PostMapping("/reserve")
    public ResponseEntity<StandardResponseDto> reserveProduct(@RequestBody OrderDto data){
        inventoryService.reserveProduct(data);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Order Placed Successfully!", null
                ),
                HttpStatus.CREATED
        );
    }
}
