package com.order.management.inventoryreservation;

import com.order.management.inventoryreservation.dto.OrderDto;
import com.order.management.inventoryreservation.dto.ProductDto;
import com.order.management.inventoryreservation.entity.Order;
import com.order.management.inventoryreservation.entity.Product;
import com.order.management.inventoryreservation.repository.OrderRepository;
import com.order.management.inventoryreservation.repository.ProductRepository;
import com.order.management.inventoryreservation.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class InventoryReservationApplicationTests {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    // ==================== Service Layer Tests ====================

    @Test
    @Transactional
    void testSaveProduct_Success() {
        ProductDto productDto = ProductDto.builder()
                .productName("Test Product")
                .availableQuantity(100)
                .build();

        inventoryService.saveProduct(productDto);

        Product savedProduct = productRepository.findAll().get(0);
        assertThat(savedProduct.getProductName()).isEqualTo("Test Product");
        assertThat(savedProduct.getAvailableQuantity()).isEqualTo(100);
    }

    @Test
    @Transactional
    void testReserveProduct_Success() {
        // Create and save a product
        Product product = Product.builder()
                .productName("Test Product")
                .availableQuantity(100)
                .build();
        product = productRepository.save(product);

        // Create order request
        OrderDto orderDto = OrderDto.builder()
                .productId(product.getProductId())
                .quantity(10)
                .build();

        // Reserve product
        inventoryService.reserveProduct(orderDto);

        // Verify product quantity is reduced
        Product updatedProduct = productRepository.findById(product.getProductId()).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(90);

        // Verify order is created
        Order savedOrder = orderRepository.findAll().get(0);
        assertThat(savedOrder.getQuantity()).isEqualTo(10);
        assertThat(savedOrder.getProduct().getProductId()).isEqualTo(product.getProductId());
    }

    @Test
    @Transactional
    void testReserveProduct_ProductNotFound() {
        OrderDto orderDto = OrderDto.builder()
                .productId("non-existent-id")
                .quantity(10)
                .build();

        assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveProduct(orderDto);
        });
    }

    @Test
    @Transactional
    void testReserveProduct_NotEnoughQuantity() {
        // Create and save a product with limited quantity
        Product product = Product.builder()
                .productName("Limited Product")
                .availableQuantity(5)
                .build();
        product = productRepository.save(product);

        // Try to reserve more than available
        OrderDto orderDto = OrderDto.builder()
                .productId(product.getProductId())
                .quantity(10)
                .build();

        assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveProduct(orderDto);
        });
    }

    @Test
    @Transactional
    void testReserveProduct_MultipleOrders_ReduceStockCorrectly() {
        // Create and save a product
        Product product = Product.builder()
                .productName("Multi Order Product")
                .availableQuantity(50)
                .build();
        product = productRepository.save(product);

        // First order
        OrderDto orderDto1 = OrderDto.builder()
                .productId(product.getProductId())
                .quantity(10)
                .build();
        inventoryService.reserveProduct(orderDto1);

        // Second order
        OrderDto orderDto2 = OrderDto.builder()
                .productId(product.getProductId())
                .quantity(15)
                .build();
        inventoryService.reserveProduct(orderDto2);

        // Verify product quantity is reduced correctly
        Product updatedProduct = productRepository.findById(product.getProductId()).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(25); // 50 - 10 - 15 = 25

        // Verify both orders are created
        assertThat(orderRepository.count()).isEqualTo(2);
    }

    @Test
    @Transactional
    void testReserveProduct_ExactQuantityAvailable() {
        // Create and save a product with exact quantity needed
        Product product = Product.builder()
                .productName("Exact Quantity Product")
                .availableQuantity(10)
                .build();
        product = productRepository.save(product);

        // Reserve exact quantity
        OrderDto orderDto = OrderDto.builder()
                .productId(product.getProductId())
                .quantity(10)
                .build();
        inventoryService.reserveProduct(orderDto);

        // Verify product quantity is 0
        Product updatedProduct = productRepository.findById(product.getProductId()).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(0);
    }
}
