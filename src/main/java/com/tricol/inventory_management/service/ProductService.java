package com.tricol.inventory_management.service;


import com.tricol.inventory_management.exception.DuplicateResourceException;
import com.tricol.inventory_management.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import com.tricol.inventory_management.dto.request.create.ProductCreateRequestDTO;
import com.tricol.inventory_management.dto.response.ProductResponseDTO;
import com.tricol.inventory_management.mapper.ProductMapper;
import com.tricol.inventory_management.model.Product;
import com.tricol.inventory_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductCreateRequestDTO requestDTO){
        if (productRepository.findByReference(requestDTO.getReference()).isPresent()) {
            throw new DuplicateResourceException("Product with reference " + requestDTO.getReference() + " already exists");
        }

        // we received the request which is a dto, and to save to db, we have to make it an entity
        Product product = productMapper.toEntity(requestDTO);
        try {
            Product savedProduct = productRepository.save(product);
            // convert saved entity to dto and return it as a response
            return productMapper.toDTO(savedProduct);
        } catch (DataIntegrityViolationException e) {
            // translate DB unique constraint violation (race condition) to domain exception
            throw new DuplicateResourceException("Product with reference " + requestDTO.getReference() + " already exists", e);
        }
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " does not exist"));
        return productMapper.toDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllProducts(){
        List<ProductResponseDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO).toList();

        return products;
    }


}
