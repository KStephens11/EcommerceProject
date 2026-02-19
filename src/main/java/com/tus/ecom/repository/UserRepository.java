package com.tus.ecom.repository;

import com.tus.ecom.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> getUserEntityById(Integer id);

}
