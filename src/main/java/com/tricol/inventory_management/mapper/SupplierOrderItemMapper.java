package com.tricol.inventory_management.mapper;

import com.tricol.inventory_management.dto.response.SupplierOrderItemResponseDTO;
import com.tricol.inventory_management.model.SupplierOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierOrderItemMapper {
    SupplierOrderItemResponseDTO toDTO(SupplierOrderItem item);
    SupplierOrderItem toEntity(SupplierOrderItemResponseDTO dto);
    void updateEntity(SupplierOrderItemResponseDTO dto, @MappingTarget SupplierOrderItem entity);
}
