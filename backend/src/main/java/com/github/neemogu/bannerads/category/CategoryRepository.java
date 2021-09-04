package com.github.neemogu.bannerads.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Optional<Category> findByReqName(String reqName);
    Optional<Category> findByNameAndIdIsNot(String name, Integer id);
    Optional<Category> findByReqNameAndIdIsNot(String reqName, Integer id);
    Optional<Category> findByIdAndDeletedFalse(Integer id);
    boolean existsByIdAndDeletedFalse(Integer id);
}
