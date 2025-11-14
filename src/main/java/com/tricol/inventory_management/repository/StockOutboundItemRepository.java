package com.tricol.inventory_management.repository;

import com.tricol.inventory_management.model.StockOutboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutboundItemRepository extends JpaRepository<StockOutboundItem, Long> {
}