package com.xunim.catalogo.service;

import com.xunim.catalogo.dto.OrderItemRequestDTO;
import com.xunim.catalogo.dto.OrderItemResponseDTO;
import com.xunim.catalogo.dto.OrderRequestDTO;
import com.xunim.catalogo.dto.OrderResponseDTO;
import com.xunim.catalogo.entity.Order;
import com.xunim.catalogo.entity.OrderItem;
import com.xunim.catalogo.entity.Product;
import com.xunim.catalogo.repository.OrderRepository;
import com.xunim.catalogo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {

        List<Long> productIds = orderRequest.getItems().stream()
                .map(OrderItemRequestDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findActiveProductByIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        //TODO: Criar função para validar Erro de estoque e criar DTO

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : orderRequest.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_EVEN);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setLineTotal(lineTotal);

            orderItems.add(orderItem);
            total = total.add(lineTotal);
        }

        order.setTotal(total.setScale(2, RoundingMode.HALF_EVEN));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return convertToResponseDTO(savedOrder);
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getTotal(),
                itemDTOs
        );
    }
}
