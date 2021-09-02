package com.github.neemogu.bannerads.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Optional<Category> findByReqName(String reqName);
    boolean existsByNameAndIdIsNot(String name, Integer id);
    boolean existsByReqNameAndIdIsNot(String reqName, Integer id);
    boolean existsByIdAndDeletedFalse(Integer id);
}
