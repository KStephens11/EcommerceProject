package com.tus.ecom.service;

import com.tus.ecom.dto.product.ProductRequestDto;
import com.tus.ecom.dto.product.ProductResponseDto;
import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto addProduct(ProductRequestDto request) {

        ProductEntity entity = this.toEntity(request);

        return this.toDto(
                productRepository.save(entity)
        );

    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponseDto updateProduct(Long id,
                                            ProductRequestDto request) {

        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setBrand(request.getBrand());
        existing.setImage(request.getImage());
        existing.setPrice(request.getPrice());
        existing.setQuantity(request.getQuantity());

        return this.toDto(
                productRepository.save(existing)
        );
    }

    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toDto);
    }

    public Optional<ProductResponseDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDto);
    }

    public Page<ProductResponseDto> getProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toDto);
    }

    public List<ProductResponseDto> getLowStockProducts(int threshold) {
        return productRepository
                .findByQuantityLessThanEqualOrderByQuantityAsc(threshold)
                .stream()
                .map(this::toDto)
                .toList();
    }


    // Mappers
    public ProductResponseDto toDto(ProductEntity entity) {

        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setBrand(entity.getBrand());
        dto.setImage(entity.getImage());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());

        return dto;
    }

    public ProductEntity toEntity(ProductRequestDto dto) {

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setBrand(dto.getBrand());
        entity.setImage(dto.getImage());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());

        return entity;
    }

}