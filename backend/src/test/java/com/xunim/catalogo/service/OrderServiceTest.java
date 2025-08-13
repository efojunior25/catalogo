package com.xunim.catalogo.service;

import com.xunim.catalogo.dto.OrderItemRequestDTO;
import com.xunim.catalogo.dto.OrderRequestDTO;
import com.xunim.catalogo.dto.OrderResponseDTO;
import com.xunim.catalogo.entity.Product;
import com.xunim.catalogo.exception.InsuffcientStockException;
import com.xunim.catalogo.repository.OrderRepository;
import com.xunim.catalogo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Café Torrado 500g", new BigDecimal("18.90"), 5, true, 0);
        product2 = new Product(2L, "Garrafa Térmica 1L", new BigDecimal("79.90"), 2, true,0);
    }

    @Test
    void shouldAllowOrderWithSufficientStock() {

        OrderItemRequestDTO item1 = new OrderItemRequestDTO(1L, 2);
        OrderItemRequestDTO item2 = new OrderItemRequestDTO(2l,1);
        OrderRequestDTO orderRequest = new OrderRequestDTO(Arrays.asList(item1, item2));

        when(productRepository.findActiveProductByIds(anyList()))
                .thenReturn(Arrays.asList(product1, product2));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any()))
                .thenAnswer(invocation -> {
                    var order = invocation.getArgument(0);
                    return order;
                });

        assertDoesNotThrow(() -> orderService.createOrder(orderRequest));

        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void shouldFailWhenInsufficientStock() {

        OrderItemRequestDTO item = new OrderItemRequestDTO(2l,3);
        OrderRequestDTO orderRequest = new OrderRequestDTO(Arrays.asList(item));

        when(productRepository.findActiveProductByIds(anyList()))
                .thenReturn(Arrays.asList(product2));

        InsuffcientStockException exception = assertThrows(
                InsuffcientStockException.class,
                () -> orderService.createOrder(orderRequest)
        );

        assertFalse(exception.getStockErrors().isEmpty());
        assertEquals(2L, exception.getStockErrors().get(0).getProductId());
        assertEquals(2, exception.getStockErrors().get(0).getAvailable());

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldCalculateTotalCorrectlyWithBankingRounding() {

        Product testProduct = new Product(3L, "Produto Teste", new BigDecimal("10.33"), 10, true, 0);
        OrderItemRequestDTO item = new OrderItemRequestDTO(3L, 3);
        OrderRequestDTO orderRequest = new OrderRequestDTO(Arrays.asList(item));

        when(productRepository.findActiveProductByIds(anyList()))
                .thenReturn(Arrays.asList(testProduct));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any()))
                .thenAnswer(invocation -> {
                    var order = invocation.getArgument(0);
                    return order;
                });

        OrderResponseDTO result = orderService.createOrder(orderRequest);

        assertEquals(new BigDecimal("30.99"), result.getTotal());
    }

    @Test
    void shouldFailWithMultiplesProductOutOfStock() {

        product1.setStock(1);
        product2.setStock(0);

        OrderItemRequestDTO item1 = new OrderItemRequestDTO(1L, 2);
        OrderItemRequestDTO item2 = new OrderItemRequestDTO(2l,1);
        OrderRequestDTO orderRequest = new OrderRequestDTO(Arrays.asList(item1, item2));

        when(productRepository.findActiveProductByIds(anyList()))
                .thenReturn(Arrays.asList(product1, product2));

        InsuffcientStockException exception = assertThrows(
                InsuffcientStockException.class,
                () -> orderService.createOrder(orderRequest)
        );

        assertEquals(2, exception.getStockErrors().size());

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldRejectNonExistentProduct() {

        OrderItemRequestDTO item = new OrderItemRequestDTO(99l,3);
        OrderRequestDTO orderRequest = new OrderRequestDTO(Arrays.asList(item));

        when(productRepository.findActiveProductByIds(anyList()))
                .thenReturn(Arrays.asList());

        InsuffcientStockException exception = assertThrows(
                InsuffcientStockException.class,
                () -> orderService.createOrder(orderRequest)
        );

        assertEquals("Produto não encontrado.", exception.getStockErrors().get(0).getProductName());
    }
}
