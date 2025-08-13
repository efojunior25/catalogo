package com.xunim.catalogo.controller;

import com.xunim.catalogo.dto.ProductPageDTO;
import com.xunim.catalogo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPageDTO> getProducts(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        ProductPageDTO products = productService.findProducts(search, page, size);
        return ResponseEntity.ok(products);
    }
}
