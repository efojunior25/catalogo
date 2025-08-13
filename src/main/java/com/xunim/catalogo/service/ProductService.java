package com.xunim.catalogo.service;

import com.xunim.catalogo.dto.ProductDTO;
import com.xunim.catalogo.dto.ProductPageDTO;
import com.xunim.catalogo.entity.Product;
import com.xunim.catalogo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductPageDTO findProducts(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findActiveProductsByName(search, pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ProductPageDTO(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findActiveProductByIds(productIds);
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getActive()
        );
    }
}
