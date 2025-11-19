package com.tricol.inventory_management.repository;

import com.tricol.inventory_management.enums.OutboundStatus;
import com.tricol.inventory_management.model.StockOutbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutboundRepository extends JpaRepository<StockOutbound, Long> {
    List<StockOutbound> findByStatus(OutboundStatus status);
    List<StockOutbound> findByWorkshop(String workshop);
    boolean existsByReference(String reference);
}