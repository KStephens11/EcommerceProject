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

class ProductServiceTest {

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
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Laptop");
        savedEntity.setBrand("Apple");
        savedEntity.setPrice(BigDecimal.valueOf(1000));

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedEntity);

        ProductResponseDto result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("Apple", result.getBrand());  // was checking getName() — wrong field

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

    @Test
    void addProduct_missingName_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        assertEquals("Product name is required.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProduct_missingBrand_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        assertEquals("Brand is required.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProduct_missingCategory_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setBrand("Apple");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        assertEquals("Category is required.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProduct_negativePrice_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(-10));
        request.setQuantity(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        assertEquals("Price must be a positive value.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProduct_negativeQuantity_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(-1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        assertEquals("Stock quantity must be a positive value.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_missingName_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, request)
        );

        assertEquals("Product name is required.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_notFound_throwsException() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop");
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setQuantity(10);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> productService.updateProduct(99L, request)
        );

        assertEquals("Product not found", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_success() {
        // Input DTO
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Laptop Pro");
        request.setBrand("Apple");
        request.setCategory("Electronics");
        request.setPrice(BigDecimal.valueOf(1500));
        request.setQuantity(5);

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");
        existingProduct.setBrand("Apple");
        existingProduct.setCategory("Electronics");
        existingProduct.setPrice(BigDecimal.valueOf(1000));
        existingProduct.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId(1L);
        savedProduct.setName(request.getName());
        savedProduct.setBrand(request.getBrand());
        savedProduct.setCategory(request.getCategory());
        savedProduct.setPrice(request.getPrice());
        savedProduct.setQuantity(request.getQuantity());

        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProduct);

        ProductResponseDto result = productService.updateProduct(1L, request);

        assertNotNull(result);
        assertEquals("Laptop Pro", result.getName());
        assertEquals("Apple", result.getBrand());
        assertEquals("Electronics", result.getCategory());
        assertEquals(BigDecimal.valueOf(1500), result.getPrice());
        assertEquals(5, result.getQuantity());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void getLowStockProducts_returnsFilteredProducts() {
        int threshold = 5;

        ProductEntity p1 = new ProductEntity();
        p1.setId(1L);
        p1.setName("Mouse");
        p1.setQuantity(2);

        ProductEntity p2 = new ProductEntity();
        p2.setId(2L);
        p2.setName("Keyboard");
        p2.setQuantity(5);

        List<ProductEntity> lowStockProducts = List.of(p1, p2);

        when(productRepository.findByQuantityLessThanEqualOrderByQuantityAsc(threshold))
                .thenReturn(lowStockProducts);

        List<ProductResponseDto> result = productService.getLowStockProducts(threshold);


        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Mouse", result.get(0).getName());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals("Keyboard", result.get(1).getName());
        assertEquals(5, result.get(1).getQuantity());

        verify(productRepository, times(1))
                .findByQuantityLessThanEqualOrderByQuantityAsc(threshold);
    }

}