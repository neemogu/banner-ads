package com.github.neemogu.bannerads.banner;

import com.github.neemogu.bannerads.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    Optional<Banner> findByNameAndIdIsNot(String name, Integer id);
    Optional<Banner> findByName(String name);
    List<Banner> findAllByDeletedFalseAndCategoryIs(Category category);
    Optional<Banner> findByIdAndDeletedFalse(Integer id);
    boolean existsByIdAndDeletedFalse(Integer id);
}
