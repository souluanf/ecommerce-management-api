package dev.luanfernandes.adapter.out.persistence.repository;

import dev.luanfernandes.adapter.out.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {}
