package com.xunim.catalogo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid
    private List<OrderItemRequestDTO> items;
}
