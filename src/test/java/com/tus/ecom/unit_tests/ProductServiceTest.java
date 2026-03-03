package com.tus.ecom.unit_tests;

import com.tus.ecom.dto.product.ProductRequestDto;
import com.tus.ecom.dto.product.ProductResponseDto;
import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.repository.ProductRepository;
import com.tus.ecom.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setPrice(BigDecimal.valueOf(1000));

        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Laptop");
        savedEntity.setPrice(BigDecimal.valueOf(1000));

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedEntity);

        ProductResponseDto result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository, times(1))
                .save(any(ProductEntity.class));
    }

    @Test
    void deleteProductTest() {

        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void getAllProductsTest() {

        Pageable pageable = PageRequest.of(0, 10);

        ProductEntity entity = new ProductEntity();
        entity.setName("Laptop");

        Page<ProductEntity> page =
                new PageImpl<>(List.of(entity));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductResponseDto> result =
                productService.getAllProducts(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Laptop",
                result.getContent().getFirst().getName());
    }

    @Test
    void getProductByIdTest() {

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Laptop");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(entity));

        Optional<ProductResponseDto> result =
                productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void getProductsByNameTest() {

        Pageable pageable = PageRequest.of(0, 10);

        ProductEntity entity = new ProductEntity();
        entity.setName("Laptop");

        Page<ProductEntity> page =
                new PageImpl<>(List.of(entity));

        when(productRepository
                .findByNameContainingIgnoreCase("lap", pageable))
                .thenReturn(page);

        Page<ProductResponseDto> result =
                productService.getProductsByName("lap", pageable);

        assertEquals(1, result.getContent().size());
    }
}