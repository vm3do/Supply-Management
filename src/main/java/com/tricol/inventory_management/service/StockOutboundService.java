package com.tricol.inventory_management.service;

import com.tricol.inventory_management.dto.request.create.StockOutboundRequestDTO;
import com.tricol.inventory_management.dto.request.update.StockOutboundUpdateDTO;
import com.tricol.inventory_management.dto.response.StockOutboundResponseDTO;
import com.tricol.inventory_management.enums.OutboundStatus;
import com.tricol.inventory_management.exception.ResourceNotFoundException;
import com.tricol.inventory_management.mapper.StockOutboundMapper;
import com.tricol.inventory_management.model.Product;
import com.tricol.inventory_management.model.StockOutbound;
import com.tricol.inventory_management.model.StockOutboundItem;
import com.tricol.inventory_management.repository.ProductRepository;
import com.tricol.inventory_management.repository.StockOutboundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockOutboundService {

    private final StockOutboundRepository stockOutboundRepository;
    private final ProductRepository productRepository;
    private final StockOutboundMapper stockOutboundMapper;
    private final StockService stockService;

    public StockOutboundResponseDTO createOutbound(StockOutboundRequestDTO dto) {
        StockOutbound outbound = StockOutbound.builder()
                .reference(generateReference())
                .reason(dto.getReason())
                .workshop(dto.getWorkshop())
                .notes(dto.getNotes())
                .build();

        for (var itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));

            StockOutboundItem item = StockOutboundItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .notes(itemDto.getNotes())
                    .build();

            outbound.addItem(item);
        }

        StockOutbound saved = stockOutboundRepository.save(outbound);
        return stockOutboundMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public StockOutboundResponseDTO getById(Long id) {
        StockOutbound outbound = stockOutboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock outbound not found with id: " + id));
        return stockOutboundMapper.toDTO(outbound);
    }

    @Transactional(readOnly = true)
    public List<StockOutboundResponseDTO> getAll() {
        return stockOutboundRepository.findAll().stream()
                .map(stockOutboundMapper::toDTO)
                .toList();
    }

    public StockOutboundResponseDTO updateOutbound(Long id, StockOutboundUpdateDTO dto) {
        StockOutbound existing = stockOutboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock outbound not found with id: " + id));

        if (existing.getStatus() != OutboundStatus.DRAFT) {
            throw new IllegalStateException("Only draft outbounds can be updated");
        }

        stockOutboundMapper.updateEntity(dto, existing);
        StockOutbound saved = stockOutboundRepository.save(existing);
        return stockOutboundMapper.toDTO(saved);
    }

    public StockOutboundResponseDTO validateOutbound(Long id) {
        StockOutbound outbound = stockOutboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock outbound not found with id: " + id));

        if (outbound.getStatus() != OutboundStatus.DRAFT) {
            throw new IllegalStateException("Only draft outbounds can be validated");
        }

        for (StockOutboundItem item : outbound.getItems()) {
            Integer currentStock = stockService.getCurrentStock(item.getProduct().getId());
            if (currentStock < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + item.getProduct().getReference());
            }
        }

        for (StockOutboundItem item : outbound.getItems()) {
            stockService.processStockOutbound(
                    item.getProduct(),
                    item.getQuantity(),
                    "Stock outbound #" + outbound.getId(),
                    outbound.getReason().toString()
            );
        }

        outbound.setStatus(OutboundStatus.VALIDATED);
        StockOutbound saved = stockOutboundRepository.save(outbound);
        return stockOutboundMapper.toDTO(saved);
    }

    public StockOutboundResponseDTO cancelOutbound(Long id) {
        StockOutbound outbound = stockOutboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock outbound not found with id: " + id));

        if (outbound.getStatus() == OutboundStatus.VALIDATED) {
            throw new IllegalStateException("Validated outbounds cannot be cancelled");
        }

        outbound.setStatus(OutboundStatus.CANCELLED);
        StockOutbound saved = stockOutboundRepository.save(outbound);
        return stockOutboundMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<StockOutboundResponseDTO> getByWorkshop(String workshop) {
        return stockOutboundRepository.findByWorkshop(workshop).stream()
                .map(stockOutboundMapper::toDTO)
                .toList();
    }

    private String generateReference() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseRef = "OUT-" + date;
        
        String reference = baseRef + "-001";
        int counter = 2;
        while (stockOutboundRepository.existsByReference(reference)) {
            reference = baseRef + "-" + String.format("%03d", counter);
            counter++;
        }
        
        return reference;
    }
}