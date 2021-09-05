package com.github.neemogu.bannerads.services;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerRepository;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceUnitTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private BannerRepository bannerRepository;
    @InjectMocks
    private CategoryService service;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    public void initEach() {
        category1 = new Category(1, "Music", "music", false);
        category2 = new Category(2, "Art", "art", false);
        category3 = new Category(3, "Cars", "cars", true);
    }

    @Test
    public void givenCategoryWithNullIdAndUniqueNameAndReqName_whenSaveCategory_thenReturnEmptyOptional() {
        Category toSave = new Category(null, category1.getName(), category1.getReqName(), false);
        when(repository.save(any(Category.class))).thenReturn(category1);
        when(repository.findByName(category1.getName())).thenReturn(Optional.empty());
        when(repository.findByReqName(category1.getReqName())).thenReturn(Optional.empty());

        assertEquals(service.saveCategory(toSave), Optional.empty());
        verify(repository, times(1)).save(toSave);
    }

    @Test
    public void givenCategoryWithNullIdAndUniqueNameAndReqNameIncludeDeleted_whenSaveCategory_thenReturnEmptyOptional() {
        Category toSave = new Category(null, category3.getName(), category3.getReqName(), false);
        Category saved = new Category(4, category3.getName(), category3.getReqName(), false);
        when(repository.save(toSave)).thenReturn(saved);
        when(repository.findByName(category3.getName())).thenReturn(Optional.of(category3));
        when(repository.findByReqName(category3.getReqName())).thenReturn(Optional.of(category3));

        assertEquals(service.saveCategory(toSave), Optional.empty());
        verify(repository, times(1)).save(toSave);
        verify(repository, times(2)).delete(category3);
    }

    @Test
    public void givenCategoryWithNullIdAndNonUniqueName_whenSaveCategory_thenReturnNonEmptyOptional() {
        Category toSave = new Category(null, category1.getName(), category2.getReqName(), false);

        when(repository.findByName(category1.getName())).thenReturn(Optional.of(category1));

        assertFalse(service.saveCategory(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenCategoryWithNullIdAndNonUniqueReqName_whenSaveCategory_thenReturnNonEmptyOptional() {
        Category toSave = new Category(null, category3.getName(), category1.getReqName(), false);

        when(repository.findByName(category3.getName())).thenReturn(Optional.of(category3));
        when(repository.findByReqName(category1.getReqName())).thenReturn(Optional.of(category1));

        assertFalse(service.saveCategory(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenCategoryWithNotExistingNotNullId_whenSaveCategory_thenReturnNonEmptyOptional() {
        when(repository.existsByIdAndDeletedFalse(category1.getId())).thenReturn(false);
        when(repository.existsByIdAndDeletedFalse(category3.getId())).thenReturn(false);

        assertFalse(service.saveCategory(category1).isEmpty());
        assertFalse(service.saveCategory(category3).isEmpty());

        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenCategoryWithNotNullIdAndUniqueNameAndReqName_whenSaveCategory_thenReturnEmptyOptional() {
        Category toSave = new Category(category1.getId(), category1.getName(), category2.getReqName(), false);

        when(repository.save(any(Category.class))).thenReturn(toSave);
        when(repository.existsByIdAndDeletedFalse(category1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(category1.getName(), category1.getId())).thenReturn(Optional.empty());
        when(repository.findByReqNameAndIdIsNot(category2.getReqName(), category1.getId())).thenReturn(Optional.empty());

        assertEquals(service.saveCategory(toSave), Optional.empty());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void givenCategoryWithNotNullIdAndUniqueNameAndReqNameIncludeDeleted_whenSaveCategory_thenReturnEmptyOptional() {
        Category toSave = new Category(category1.getId(), category3.getName(), category3.getReqName(), false);

        when(repository.save(any(Category.class))).thenReturn(toSave);
        when(repository.existsByIdAndDeletedFalse(category1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(category3.getName(), category1.getId())).thenReturn(Optional.of(category3));
        when(repository.findByReqNameAndIdIsNot(category3.getReqName(), category1.getId())).thenReturn(Optional.of(category3));

        assertEquals(service.saveCategory(toSave), Optional.empty());
        verify(repository, times(1)).save(any());
        verify(repository, times(2)).delete(category3);
    }

    @Test
    public void givenCategoryWithNotNullIdAndNonUniqueName_whenSaveCategory_thenReturnNonEmptyOptional() {
        Category toSave = new Category(category1.getId(), category2.getName(), category1.getReqName(), false);

        when(repository.existsByIdAndDeletedFalse(category1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(category2.getName(), category1.getId())).thenReturn(Optional.of(category2));

        assertFalse(service.saveCategory(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenCategoryWithNotNullIdAndNonUniqueReqName_whenSaveCategory_thenReturnNonEmptyOptional() {
        Category toSave = new Category(category1.getId(), category1.getName(), category2.getReqName(), false);

        when(repository.existsByIdAndDeletedFalse(category1.getId())).thenReturn(true);
        when(repository.findByNameAndIdIsNot(category1.getName(), category1.getId())).thenReturn(Optional.empty());
        when(repository.findByReqNameAndIdIsNot(category2.getReqName(), category1.getId())).thenReturn(Optional.of(category2));

        assertFalse(service.saveCategory(toSave).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenNotExistingCategory_whenDeleteCategory_thenReturnEmptyOptional() {
        when(repository.findById(category1.getId())).thenReturn(Optional.empty());

        assertTrue(service.deleteCategory(category1.getId()).isEmpty());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void givenCategoryWithoutAnyBanners_whenDeleteCategory_thenReturnEmptyOptional() {
        when(repository.findById(category1.getId())).thenReturn(Optional.of(category1));
        when(bannerRepository.findAllByDeletedFalseAndCategoryIs(category1)).thenReturn(Collections.emptyList());

        assertTrue(service.deleteCategory(category1.getId()).isEmpty());
        verify(repository, times(1)).save(any());
        assertTrue(category1.getDeleted());
    }

    @Test
    public void givenCategoryWhichHasBanners_whenDeleteCategory_thenReturnStringContainingBannersIds() {
        Banner banner1 = new Banner(1, "ban1", 5.55, category1, "banner1", false);
        Banner banner2 = new Banner(2, "ban2", 3.33, category1, "banner2", false);
        List<Banner> banners = List.of(banner1, banner2);

        when(repository.findById(category1.getId())).thenReturn(Optional.of(category1));
        when(bannerRepository.findAllByDeletedFalseAndCategoryIs(category1)).thenReturn(banners);

        Optional<String> result = service.deleteCategory(category1.getId());
        assertFalse(category1.getDeleted());
        verify(repository, times(0)).save(any());
        assertFalse(result.isEmpty());
        assertTrue(result.get().contains("1, 2"));
    }

    @Test
    public void givenIdOfExistingNotDeletedCategory_whenGetCategoryById_thenReturnThisCategory() {
        when(repository.findByIdAndDeletedFalse(category1.getId())).thenReturn(Optional.of(category1));
        assertEquals(service.getSpecificCategory(category1.getId()).orElse(null), category1);
    }

    @Test
    public void givenIdOfNotExistingCategory_whenGetCategoryById_thenReturnEmptyOptional() {
        when(repository.findByIdAndDeletedFalse(category1.getId())).thenReturn(Optional.empty());
        assertTrue(service.getSpecificCategory(category1.getId()).isEmpty());
    }
}
