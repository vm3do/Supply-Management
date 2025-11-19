package com.tricol.inventory_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutboundItemResponseDTO {
    private Long id;
    private Long productId;
    private String productReference;
    private String productName;
    private Integer quantity;
    private String notes;
}