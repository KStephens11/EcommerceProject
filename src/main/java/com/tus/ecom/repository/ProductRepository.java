package com.tus.ecom.repository;

import com.tus.ecom.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    ProductEntity getProductEntitiesById(Long id);

    Optional<Boolean> findProductEntitiesById(Long id);
}
