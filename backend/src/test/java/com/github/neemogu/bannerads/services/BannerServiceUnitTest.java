package com.github.neemogu.bannerads.services;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerRepository;
import com.github.neemogu.bannerads.banner.BannerService;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BannerServiceUnitTest {
    @Mock
    private BannerRepository repository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BannerService service;

    private Banner banner1;
    private Banner banner2;
    private Banner banner3;
    private Category category;

    @BeforeEach
    public void init() {
        category = new Category(1, "Music", "music", false);
        banner1 = new Banner(1, "ban1", 5.55, category, "banner1", false);
        banner2 = new Banner(2, "ban2", 3.33, category, "banner2", false);
        banner3 = new Banner(3, "ban3", 2.22, category, "banner3", true);
    }

    @Test
    public void givenBannerWithNotExistingCategory_whenSaveBanner_thenReturnNonEmptyOptional() {
        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(false);

        assertFalse(service.saveBanner(banner1).isEmpty());
    }

    @Test
    public void givenBannerWithNullIdAndUniqueName_whenSaveBanner_thenReturnEmptyOptional() {
        Banner toSave = new Banner(null, banner1.getName(), banner1.getPrice(),
                banner1.getCategory(), banner1.getContent(), false);
        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.save(any(Banner.class))).thenReturn(banner1);
        when(repository.findByName(banner1.getName())).thenReturn(Optional.empty());

        assertEquals(service.saveBanner(toSave), Optional.empty());
        verify(repository, times(1)).save(toSave);
    }

    @Test
    public void givenBannerWithNullIdAndUniqueNameIncludeDeleted_whenSaveBanner_thenReturnEmptyOptional() {
        Banner toSave = new Banner(null, banner3.getName(), banner3.getPrice(),
                banner3.getCategory(), banner3.getContent(), false);
        Banner saved = new Banner(4, banner3.getName(), banner3.getPrice(),
                banner3.getCategory(), banner3.getContent(), false);
        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.save(toSave)).thenReturn(saved);
        when(repository.findByName(banner3.getName())).thenReturn(Optional.of(banner3));

        assertEquals(service.saveBanner(toSave), Optional.empty());
        verify(repository, times(1)).save(toSave);
        verify(repository, times(1)).delete(banner3);
    }

    @Test
    public void givenBannerWithNullIdAndNonUniqueName_whenSaveBanner_thenReturnNonEmptyOptional() {
        Banner toSave = new Banner(null, banner2.getName(), banner2.getPrice(),
                banner2.getCategory(), banner2.getContent(), false);

        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.findByName(banner2.getName())).thenReturn(Optional.of(banner2));

        assertFalse(service.saveBanner(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenBannerWithNotExistingNotNullId_whenSaveBanner_thenReturnNonEmptyOptional() {
        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.existsByIdAndDeletedFalse(banner1.getId())).thenReturn(false);
        when(repository.existsByIdAndDeletedFalse(banner3.getId())).thenReturn(false);

        assertFalse(service.saveBanner(banner1).isEmpty());
        assertFalse(service.saveBanner(banner3).isEmpty());

        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenBannerWithNotNullIdAndUniqueName_whenSaveBanner_thenReturnEmptyOptional() {
        Banner toSave = new Banner(banner1.getId(), banner1.getName(), banner1.getPrice(),
                banner1.getCategory(), banner1.getContent(), false);

        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.save(any(Banner.class))).thenReturn(toSave);
        when(repository.existsByIdAndDeletedFalse(banner1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(banner1.getName(), banner1.getId())).thenReturn(Optional.empty());

        assertEquals(service.saveBanner(toSave), Optional.empty());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void givenBannerWithNotNullIdAndUniqueNameIncludeDeleted_whenSaveBanner_thenReturnEmptyOptional() {
        Banner toSave = new Banner(banner1.getId(), banner3.getName(), banner1.getPrice(),
                banner1.getCategory(), banner1.getContent(), false);

        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.save(any(Banner.class))).thenReturn(toSave);
        when(repository.existsByIdAndDeletedFalse(banner1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(banner3.getName(), banner1.getId())).thenReturn(Optional.of(banner3));

        assertEquals(service.saveBanner(toSave), Optional.empty());
        verify(repository, times(1)).save(any());
        verify(repository, times(1)).delete(banner3);
    }

    @Test
    public void givenBannerWithNotNullIdAndNonUniqueName_whenSaveBanner_thenReturnNonEmptyOptional() {
        Banner toSave = new Banner(banner1.getId(), banner2.getName(), banner1.getPrice(),
                banner1.getCategory(), banner1.getContent(), false);

        when(categoryRepository.existsByIdAndDeletedFalse(category.getId())).thenReturn(true);
        when(repository.existsByIdAndDeletedFalse(banner1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(banner2.getName(), banner1.getId())).thenReturn(Optional.of(banner2));

        assertFalse(service.saveBanner(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenNotExistingBanner_shouldDoNothing() {
        when(repository.findById(banner1.getId())).thenReturn(Optional.empty());

        service.deleteBanner(banner1.getId());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenExistingBanner_shouldDeleteBannerBySettingDeletedPropertyToTrue() {
        when(repository.findById(banner1.getId())).thenReturn(Optional.of(banner1));

        service.deleteBanner(banner1.getId());
        verify(repository, times(1)).save(any());
        assertTrue(banner1.getDeleted());
    }

    @Test
    public void givenIdOfExistingNotDeletedBanner_whenGetBannerById_thenReturnThisBanner() {
        when(repository.findByIdAndDeletedFalse(banner1.getId())).thenReturn(Optional.of(banner1));
        assertEquals(service.getSpecificBanner(banner1.getId()).orElse(null), banner1);
    }

    @Test
    public void givenIdOfNotExistingBanner_whenGetBannerById_thenReturnEmptyOptional() {
        when(repository.findByIdAndDeletedFalse(banner1.getId())).thenReturn(Optional.empty());
        assertTrue(service.getSpecificBanner(banner1.getId()).isEmpty());
    }
}
