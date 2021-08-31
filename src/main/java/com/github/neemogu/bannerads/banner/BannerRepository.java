package com.github.neemogu.bannerads.banner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    boolean existsByNameAndIdIsNot(String name, Integer id);
    Optional<Banner> findByName(String name);
}
