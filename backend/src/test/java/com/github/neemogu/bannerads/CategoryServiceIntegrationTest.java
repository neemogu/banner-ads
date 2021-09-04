package com.github.neemogu.bannerads;

import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryFetchParameters;
import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.category.CategoryService;
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
public class CategoryServiceIntegrationTest {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryService service;

    @BeforeEach
    public void initDB() {
        Category cat1 = new Category(null, "Music", "music", false);
        Category cat2 = new Category(null, "Art", "art", false);
        Category cat3 = new Category(null, "Martial", "martial", false);
        Category cat4 = new Category(null, "Artistic", "artistic", true);
        repository.saveAll(List.of(cat1, cat2, cat3, cat4));
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldReturnCorrectPageCount() {
        CategoryFetchParameters parameters = CategoryFetchParameters.builder().pageSize(2).build();
        assertEquals(service.getCategoryListPageCount(parameters), 2);
        parameters = CategoryFetchParameters.builder().pageSize(3).build();
        assertEquals(service.getCategoryListPageCount(parameters), 1);
    }

    @Test
    public void shouldReturnCorrectPagedList() {
        CategoryFetchParameters parameters = CategoryFetchParameters.builder().pageSize(2).page(0).build();
        List<String> result = service.getCategoryList(parameters)
                .stream().map(Category::getName).collect(Collectors.toList());
        assertEquals(result.size(), 2);
        assertTrue(result.contains("Music"));
        assertTrue(result.contains("Art"));

        parameters = CategoryFetchParameters.builder().pageSize(2).page(1).build();
        List<Category> result2 = service.getCategoryList(parameters);
        assertEquals(result2.size(), 1);
        assertEquals(result2.get(0).getName(), "Martial");
    }

    @Test
    public void givenSearchNameString_shouldReturnPagedListOfCategoriesContainingThisString() {
        String searchName = "Art";
        CategoryFetchParameters parameters = CategoryFetchParameters.builder()
                .pageSize(4).page(0).searchName(searchName).build();
        List<String> result = service.getCategoryList(parameters)
                .stream().map(Category::getName).collect(Collectors.toList());
        System.out.println(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains("Art"));
        assertTrue(result.contains("Martial"));

        parameters = CategoryFetchParameters.builder().pageSize(1).searchName(searchName).build();
        assertEquals(service.getCategoryListPageCount(parameters), 2);
    }
}
