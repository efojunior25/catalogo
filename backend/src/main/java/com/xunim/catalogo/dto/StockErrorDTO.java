package com.xunim.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockErrorDTO {
    private Long productId;
    private Integer available;
    private String productName;

    public StockErrorDTO(Long productId, Integer available) {
        this.productId = productId;
        this.available = available;
    }
}
