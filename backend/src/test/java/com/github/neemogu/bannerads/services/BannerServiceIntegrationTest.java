package com.github.neemogu.bannerads.services;

import com.github.neemogu.bannerads.banner.*;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.util.SortDirection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BannerServiceIntegrationTest {
    @Autowired
    private BannerService service;
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;

    @BeforeEach
    public void initDB() {
        category1 = categoryRepository.save(
                new Category(null, "Music", "music", false)
        );
        category2 = categoryRepository.save(
                new Category(null, "Technology", "tech", false)
        );

        Banner banner1 = new Banner(null, "Pink Floyd", 3.49, category1, "PF", true);
        Banner banner2 = new Banner(null, "The Beatles", 3.49, category1, "BB", false);
        Banner banner3 = new Banner(null, "Les Paul", 10.99, category1, "LP", false);
        Banner banner4 = new Banner(null, "Artificial Intelligence", 5.55, category2, "AI", true);
        Banner banner5 = new Banner(null, "SpaceX", 8.29, category2, "SX", false);
        Banner banner6 = new Banner(null, "BleSs", 10.99, category2, "BS", false);
        bannerRepository.saveAll(List.of(banner1, banner2, banner3, banner4, banner5, banner6));
    }

    @AfterEach
    public void tearDown() {
        bannerRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldReturnCorrectPageCount() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(2).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(4).build();
        assertEquals(service.getBannerListPageCount(parameters1), 2);
        assertEquals(service.getBannerListPageCount(parameters2), 1);
    }

    @Test
    public void shouldReturnCorrectPagedList() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(3).page(0).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(3).page(1).build();

        List<String> res1 = service.getBannerList(parameters1).stream().map(Banner::getName).collect(Collectors.toList());
        assertEquals(res1.size(), 3);
        assertTrue(res1.containsAll(List.of("The Beatles", "Les Paul", "SpaceX")));

        List<String> res2 = service.getBannerList(parameters2).stream().map(Banner::getName).collect(Collectors.toList());
        assertEquals(res2.size(), 1);
        assertTrue(res2.contains("BleSs"));
    }

    @Test
    public void givenToSortByNameAndSortedDirection_shouldReturnSortedList() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(6).page(0)
                .sortBy(BannerSortBy.NAME).sortDirection(SortDirection.ASC).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(6).page(0)
                .sortBy(BannerSortBy.NAME).sortDirection(SortDirection.DESC).build();
        List<String> res1 = service.getBannerList(parameters1).stream().map(Banner::getName).collect(Collectors.toList());
        List<String> res2 = service.getBannerList(parameters2).stream().map(Banner::getName).collect(Collectors.toList());

        assertEquals(res1.size(), 4);
        assertEquals(res2.size(), 4);

        assertEquals(res1.get(0), "BleSs");
        assertEquals(res1.get(1), "Les Paul");
        assertEquals(res1.get(2), "SpaceX");
        assertEquals(res1.get(3), "The Beatles");

        assertEquals(res2.get(0), "The Beatles");
        assertEquals(res2.get(1), "SpaceX");
        assertEquals(res2.get(2), "Les Paul");
        assertEquals(res2.get(3), "BleSs");
    }

    @Test
    public void givenToSortByPriceAndSortedDirection_shouldReturnSortedList() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(6).page(0)
                .sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.ASC).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(6).page(0)
                .sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.DESC).build();
        List<String> res1 = service.getBannerList(parameters1).stream().map(Banner::getName).collect(Collectors.toList());
        List<String> res2 = service.getBannerList(parameters2).stream().map(Banner::getName).collect(Collectors.toList());

        assertEquals(res1.size(), 4);
        assertEquals(res2.size(), 4);

        assertEquals(res1.get(0), "The Beatles");
        assertEquals(res1.get(1), "SpaceX");
        assertTrue(res1.get(2).equals("Les Paul") || res1.get(2).equals("BleSs"));
        assertTrue(res1.get(3).equals("Les Paul") || res1.get(3).equals("BleSs"));

        assertTrue(res2.get(0).equals("Les Paul") || res2.get(0).equals("BleSs"));
        assertTrue(res2.get(1).equals("Les Paul") || res2.get(1).equals("BleSs"));
        assertEquals(res2.get(2), "SpaceX");
        assertEquals(res2.get(3), "The Beatles");
    }

    @Test
    public void givenSearchNameString_shouldReturnPagedListOfBannersContainingThisString() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(6).page(0)
                .searchName("Les").build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(1)
                .searchName("Les").build();

        List<String> result = service.getBannerList(parameters1)
                .stream().map(Banner::getName).collect(Collectors.toList());
        assertEquals(result.size(), 3);
        assertTrue(result.containsAll(List.of("Les Paul", "BleSs", "The Beatles")));

        assertEquals(service.getBannerListPageCount(parameters2), 3);
    }

    @Test
    public void givenCategoryId_shouldReturnPagedListOfBannersOfThisCategory() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(6).page(0)
                .categoryId(category1.getId()).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(1)
                .categoryId(category1.getId()).build();

        List<String> result = service.getBannerList(parameters1)
                .stream().map(Banner::getName).collect(Collectors.toList());
        assertEquals(result.size(), 2);
        assertTrue(result.containsAll(List.of("Les Paul", "The Beatles")));

        assertEquals(service.getBannerListPageCount(parameters2), 2);
    }

    @Test
    public void givenCategoryIdAndSearchNameStringAndSortProperties_shouldReturnPagedListOfBannersSatisfyingThemAll() {
        BannerFetchParameters parameters1 = BannerFetchParameters.builder().pageSize(6).page(0)
                .categoryId(category1.getId()).searchName("les").sortBy(BannerSortBy.NAME)
                .sortDirection(SortDirection.ASC).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder().pageSize(1)
                .categoryId(category2.getId()).searchName("spa").build();
        List<String> result = service.getBannerList(parameters1).stream().map(Banner::getName).collect(Collectors.toList());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), "Les Paul");
        assertEquals(result.get(1), "The Beatles");

        assertEquals(service.getBannerListPageCount(parameters2), 1);
    }
}
