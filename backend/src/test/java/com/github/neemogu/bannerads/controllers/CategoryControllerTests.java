package com.github.neemogu.bannerads.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryController;
import com.github.neemogu.bannerads.category.CategoryFetchParameters;
import com.github.neemogu.bannerads.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CategoryController.class)
public class CategoryControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService service;

    private Category category1;
    private Category category2;
    private List<Category> categories;

    @BeforeEach
    public void init() {
        category1 = new Category(1, "Music", "music", false);
        category2 = new Category(2, "Amusing", "amusing", false);
        categories = List.of(category1, category2);
    }

    @Test
    public void givenTooMuchPageSize_whenGetCategoriesList_thenReturnHTTPBadRequest() throws Exception {
        String tooMuchPageSize = String.valueOf(CategoryController.maxPageSize + 1);
        mockMvc.perform(get("/categories/list")
                .param("pageSize", tooMuchPageSize))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).getCategoryList(any());
    }

    @Test
    public void whenGetCategoriesList_thenReturnHTTPOkWithCategoriesList() throws Exception {
        when(service.getCategoryList(any())).thenReturn(categories);
        String searchName = "mus";
        int pageSize = CategoryController.maxPageSize / 2;
        int page = 1;
        MvcResult result = mockMvc.perform(get("/categories/list")
                .param("searchName", searchName)
                .param("pageSize", String.valueOf(pageSize))
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(categories));

        ArgumentCaptor<CategoryFetchParameters> parametersCaptor = ArgumentCaptor.forClass(CategoryFetchParameters.class);
        verify(service, times(1)).getCategoryList(parametersCaptor.capture());
        assertThat(parametersCaptor.getValue().getPage()).isEqualTo(page);
        assertThat(parametersCaptor.getValue().getPageSize()).isEqualTo(pageSize);
        assertThat(parametersCaptor.getValue().getSearchName()).isEqualTo(searchName);
    }

    @Test
    public void givenTooMuchPageSize_whenGetCategoriesListPageCount_thenReturnHTTPBadRequest() throws Exception {
        String tooMuchPageSize = String.valueOf(CategoryController.maxPageSize + 1);
        mockMvc.perform(get("/categories/pages")
                .param("pageSize", tooMuchPageSize))
                .andExpect(status().isBadRequest());
        verify(service, times(0)).getCategoryListPageCount(any());
    }

    @Test
    public void whenGetCategoriesListPageCount_thenReturnHTTPOkWithCategoriesListPageCount() throws Exception {
        when(service.getCategoryListPageCount(any())).thenReturn(4L);
        String searchName = "mus";
        int pageSize = CategoryController.maxPageSize / 2;
        MvcResult result = mockMvc.perform(get("/categories/pages")
                .param("searchName", searchName)
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualTo("4");

        ArgumentCaptor<CategoryFetchParameters> parametersCaptor = ArgumentCaptor.forClass(CategoryFetchParameters.class);
        verify(service, times(1)).getCategoryListPageCount(parametersCaptor.capture());
        assertThat(parametersCaptor.getValue().getSearchName()).isEqualTo(searchName);
        assertThat(parametersCaptor.getValue().getPageSize()).isEqualTo(pageSize);
    }

    @Test
    public void givenIDOfNotExistingCategory_whenGetSpecificCategory_thenReturnHTTPNotFound() throws Exception {
        when(service.getSpecificCategory(category1.getId())).thenReturn(Optional.empty());
        mockMvc.perform(get("/categories/{id}", category1.getId()))
                .andExpect(status().isNotFound());
        verify(service, times(1)).getSpecificCategory(any());
    }

    @Test
    public void givenIDOfCategory_whenGetSpecificCategory_thenReturnHTTPOkWithACategory() throws Exception {
        when(service.getSpecificCategory(category1.getId())).thenReturn(Optional.of(category1));
        MvcResult result = mockMvc.perform(get("/categories/{id}", category1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(category1));

        verify(service, times(1)).getSpecificCategory(any());
    }

    @Test
    public void givenIDOfCategoryWithBanners_whenDeleteCategory_thenReturnHTTPConflictWithErrorMessage()
    throws Exception{
        when(service.deleteCategory(category1.getId())).thenReturn(Optional.of("IDs: [1, 2, 3]"));
        mockMvc.perform(delete("/categories/{id}", category1.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("IDs: [1, 2, 3]"));
    }

    @Test
    public void givenIDOfCategoryWithoutBanners_whenDeleteCategory_thenReturnHTTPNoContent()
    throws Exception {
        when(service.deleteCategory(category1.getId())).thenReturn(Optional.empty());
        mockMvc.perform(delete("/categories/{id}", category1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenNotValidCategory_whenAddOrUpdateCategory_thenReturnErrorMap() throws Exception {
        Category notValidCategory = new Category(1, "", null, false);

        MvcResult postResult = mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidCategory)))
                .andExpect(status().isBadRequest())
                .andReturn();
        MvcResult putResult = mockMvc.perform(put("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidCategory)))
                .andExpect(status().isBadRequest())
                .andReturn();
        Map<String, String> expectedErrorMap = new TreeMap<>();
        expectedErrorMap.put("name", "Category name must be from 1 to 255 symbols length");
        expectedErrorMap.put("reqName", "Category request name cannot be null");
        String expectedErrorString = objectMapper.writeValueAsString(expectedErrorMap);

        assertThat(postResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expectedErrorString);
        assertThat(putResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expectedErrorString);
        verify(service, times(0)).saveCategory(any());
    }

    @Test
    public void whenSaveViolatingCategory_thenReturnHTTPConflictWithErrorMessage() throws Exception {
        when(service.saveCategory(any())).thenReturn(Optional.of("ERROR"));

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("ERROR"));
        mockMvc.perform(put("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("ERROR"));
    }

    @Test
    public void whenSaveCorrectCategory_thenReturnHTTPOkWithOkMessage() throws Exception {
        when(service.saveCategory(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        mockMvc.perform(put("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
