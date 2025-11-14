package com.tricol.inventory_management.dto.response;

import com.tricol.inventory_management.enums.OutboundReason;
import com.tricol.inventory_management.enums.OutboundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutboundResponseDTO {
    private Long id;
    private String reference;
    private OutboundReason reason;
    private OutboundStatus status;
    private String workshop;
    private String notes;
    private List<StockOutboundItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}