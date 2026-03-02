package com.tus.ecom.controller;

import com.tus.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.tus.ecom.model.ProductEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductEntity>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @PostMapping
    public ResponseEntity<ProductEntity> addProduct(@RequestBody ProductEntity product) {
        return ResponseEntity.ok(productService.addProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductEntity product) {

        product.setId(id);
        return ResponseEntity.ok(productService.updateProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name")
    public ResponseEntity<Page<ProductEntity>> searchProducts(@RequestParam(required = false) String name, Pageable pageable) {

        Page<ProductEntity> result;

        if (name == null || name.isBlank()) {
            result = productService.getAllProducts(pageable);
        } else {
            result = productService.getProductsByName(name, pageable);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/products/";

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir + filename);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return ResponseEntity.ok("/uploads/products/" + filename);
    }

    @GetMapping("/low-stock")
    public List<ProductEntity> getLowStock(@RequestParam(defaultValue = "5") int threshold) {
        return productService.getLowStockProducts(threshold);
    }

}
