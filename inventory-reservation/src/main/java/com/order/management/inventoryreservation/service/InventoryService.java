package com.order.management.inventoryreservation.service;

import com.order.management.inventoryreservation.dto.OrderDto;
import com.order.management.inventoryreservation.dto.ProductDto;
import com.order.management.inventoryreservation.entity.Order;
import com.order.management.inventoryreservation.entity.Product;
import com.order.management.inventoryreservation.exception.EntryNotFoundException;
import com.order.management.inventoryreservation.repository.OrderRepository;
import com.order.management.inventoryreservation.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void reserveProduct(OrderDto data){

        // Lock the product row using pessimistic lock
        Product product = productRepository.findByIdForUpdate(data.getProductId());

        // check the product availability
        if(product == null){
            throw new EntryNotFoundException("Product not found for id " + data.getProductId());
        }

        // check the product quantity
        if (product.getAvailableQuantity() < data.getQuantity()){
            throw new RuntimeException("Not enough available quantity for product " + product.getProductId());
        }

        // reduce the stock
        product.setAvailableQuantity(product.getAvailableQuantity() - data.getQuantity());
        productRepository.save(product);

        // create order
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(data.getQuantity());
        orderRepository.save(order);
    }

    public void saveProduct(ProductDto data){
        productRepository.save(toProduct(data));
    }

    // product dto to entity
    private Product toProduct(ProductDto data){
        return Product.builder()
                .productName(data.getProductName())
                .availableQuantity(data.getAvailableQuantity())
                .build();
    }
}
