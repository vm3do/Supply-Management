package com.tricol.inventory_management.dto.request.update;

import com.tricol.inventory_management.enums.OutboundReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutboundUpdateDTO {
    private OutboundReason reason;
    private String workshop;
    private String notes;
}