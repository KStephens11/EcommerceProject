package com.tus.ecom.unit_tests;

import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.repository.ProductRepository;
import com.tus.ecom.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void addProductTest() {

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Laptop");

        when(productRepository.save(product)).thenReturn(product);

        ProductEntity result = productService.addProduct(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProductTest() {

        Long productId = 1L;

        doNothing().when(productRepository).deleteById(productId);

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void updateProductTest() {

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Phone");

        when(productRepository.save(product)).thenReturn(product);

        ProductEntity result = productService.updateProduct(product);

        assertNotNull(result);
        assertEquals("Phone", result.getName());

        verify(productRepository).save(product);
    }

    @Test
    void getAllProductsTest() {

        Pageable pageable = PageRequest.of(0, 10);

        ProductEntity product1 = new ProductEntity();
        product1.setName("Laptop");

        ProductEntity product2 = new ProductEntity();
        product2.setName("Phone");

        Page<ProductEntity> page =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductEntity> result = productService.getAllProducts(pageable);

        assertEquals(2, result.getContent().size());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getProductByIdTest() {

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Laptop");

        when(productRepository.getProductEntitiesById(1L))
                .thenReturn(product);

        ProductEntity result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository).getProductEntitiesById(1L);
    }

    @Test
    void getProductsByNameTest() {

        Pageable pageable = PageRequest.of(0, 10);

        ProductEntity product = new ProductEntity();
        product.setName("Laptop");

        Page<ProductEntity> page =
                new PageImpl<>(List.of(product));

        when(productRepository
                .findByNameContainingIgnoreCase("lap", pageable))
                .thenReturn(page);

        Page<ProductEntity> result =
                productService.getProductsByName("lap", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository)
                .findByNameContainingIgnoreCase("lap", pageable);
    }
}