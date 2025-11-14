package com.tricol.inventory_management.mapper;

import com.tricol.inventory_management.dto.request.update.StockOutboundUpdateDTO;
import com.tricol.inventory_management.dto.response.StockOutboundItemResponseDTO;
import com.tricol.inventory_management.dto.response.StockOutboundResponseDTO;
import com.tricol.inventory_management.model.StockOutbound;
import com.tricol.inventory_management.model.StockOutboundItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StockOutboundMapper {

    StockOutboundResponseDTO toDTO(StockOutbound stockOutbound);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.reference", target = "productReference")
    @Mapping(source = "product.name", target = "productName")
    StockOutboundItemResponseDTO toItemDTO(StockOutboundItem item);

    void updateEntity(StockOutboundUpdateDTO dto, @MappingTarget StockOutbound entity);
}