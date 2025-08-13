package com.xunim.catalogo.exception;

import com.xunim.catalogo.dto.StockErrorDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class InsuffcientStockException extends RuntimeException {
    private final List<StockErrorDTO> stockErrors;

    public InsuffcientStockException(List<StockErrorDTO> stockErrors) {
        super("Estoque insuficiente para alguns itens");
        this.stockErrors = stockErrors;
    }
}
