package com.tus.ecom.service;

import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity addProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductEntity updateProduct(ProductEntity updatedProduct) {

        ProductEntity existing = productRepository
                .findById(updatedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(updatedProduct.getName());
        existing.setBrand(updatedProduct.getBrand());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());

        // Only update image if it's not blank
        if (updatedProduct.getImage() != null && !updatedProduct.getImage().isBlank()) {
            existing.setImage(updatedProduct.getImage());
        }

        return productRepository.save(existing);
    }

    public Page<ProductEntity> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.getProductEntitiesById(id);
    }

    public Page<ProductEntity> getProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }


    public List<ProductEntity> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThanEqualOrderByQuantityAsc(threshold);
    }
}