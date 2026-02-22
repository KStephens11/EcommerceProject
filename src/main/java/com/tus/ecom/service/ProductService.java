package com.tus.ecom.service;

import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    ProductRepository productRepository;

    @Autowired
    ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity addProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductEntity updateProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public List<ProductEntity> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).getContent();
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.getProductEntitiesById(id);
    }
}