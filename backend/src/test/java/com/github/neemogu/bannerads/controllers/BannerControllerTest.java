package com.github.neemogu.bannerads.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerController;
import com.github.neemogu.bannerads.banner.BannerFetchParameters;
import com.github.neemogu.bannerads.banner.BannerService;
import com.github.neemogu.bannerads.category.Category;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BannerController.class)
public class BannerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BannerService service;

    private Banner b1;
    private Banner b2;
    private Category category;
    private List<Banner> banners;

    @BeforeEach
    public void init() {
        category = new Category(1, "Music", "music", false);
        b1 = new Banner(1, "The Beatles", 5.55, category, "btls", false);
        b2 = new Banner(2, "Les Paul", 10.99, category, "lespaul", false);
        banners = List.of(b1, b2);
    }

    @Test
    public void givenTooMuchPageSize_whenGetBannersList_thenReturnHTTPBadRequest() throws Exception {
        String tooMuchPageSize = String.valueOf(BannerController.maxPageSize + 1);
        mockMvc.perform(get("/banners/list")
                .param("pageSize", tooMuchPageSize))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).getBannerList(any());
    }

    @Test
    public void whenGetBannersList_thenReturnHTTPOkWithBannersList() throws Exception {
        when(service.getBannerList(any())).thenReturn(banners);
        String searchName = "les";
        int pageSize = BannerController.maxPageSize / 2;
        int page = 1;
        MvcResult result = mockMvc.perform(get("/banners/list")
                .param("searchName", searchName)
                .param("pageSize", String.valueOf(pageSize))
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(banners));

        ArgumentCaptor<BannerFetchParameters> parametersCaptor = ArgumentCaptor.forClass(BannerFetchParameters.class);
        verify(service, times(1)).getBannerList(parametersCaptor.capture());
        assertThat(parametersCaptor.getValue().getPage()).isEqualTo(page);
        assertThat(parametersCaptor.getValue().getPageSize()).isEqualTo(pageSize);
        assertThat(parametersCaptor.getValue().getSearchName()).isEqualTo(searchName);
    }

    @Test
    public void givenTooMuchPageSize_whenGetBannersListPageCount_thenReturnHTTPBadRequest() throws Exception {
        String tooMuchPageSize = String.valueOf(BannerController.maxPageSize + 1);
        mockMvc.perform(get("/banners/pages")
                .param("pageSize", tooMuchPageSize))
                .andExpect(status().isBadRequest());
        verify(service, times(0)).getBannerListPageCount(any());
    }

    @Test
    public void whenGetBannersListPageCount_thenReturnHTTPOkWithBannersListPageCount() throws Exception {
        when(service.getBannerListPageCount(any())).thenReturn(4L);
        String searchName = "les";
        int pageSize = BannerController.maxPageSize / 2;
        MvcResult result = mockMvc.perform(get("/banners/pages")
                .param("searchName", searchName)
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualTo("4");

        ArgumentCaptor<BannerFetchParameters> parametersCaptor = ArgumentCaptor.forClass(BannerFetchParameters.class);
        verify(service, times(1)).getBannerListPageCount(parametersCaptor.capture());
        assertThat(parametersCaptor.getValue().getSearchName()).isEqualTo(searchName);
        assertThat(parametersCaptor.getValue().getPageSize()).isEqualTo(pageSize);
    }

    @Test
    public void givenIDOfNotExistingBanner_whenGetSpecificBanner_thenReturnHTTPNotFound() throws Exception {
        when(service.getSpecificBanner(b1.getId())).thenReturn(Optional.empty());
        mockMvc.perform(get("/banners/{id}", b1.getId()))
                .andExpect(status().isNotFound());
        verify(service, times(1)).getSpecificBanner(any());
    }

    @Test
    public void givenIDOfBanner_whenGetSpecificBanner_thenReturnHTTPOkWithABanner() throws Exception {
        when(service.getSpecificBanner(b1.getId())).thenReturn(Optional.of(b1));
        MvcResult result = mockMvc.perform(get("/banners/{id}", b1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(b1));

        verify(service, times(1)).getSpecificBanner(any());
    }

    @Test
    public void givenBannerID_whenDeleteBanner_thenReturnHTTPNoContent()
            throws Exception {
        mockMvc.perform(delete("/banners/{id}", b1.getId()))
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteBanner(b1.getId());
    }

    @Test
    public void givenNotValidBanner_whenAddOrUpdateBanner_thenReturnErrorMap() throws Exception {
        Banner notValidBanner = new Banner(1, "", -1.0, null, null, false);

        MvcResult postResult = mockMvc.perform(post("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidBanner)))
                .andExpect(status().isBadRequest())
                .andReturn();
        MvcResult putResult = mockMvc.perform(put("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidBanner)))
                .andExpect(status().isBadRequest())
                .andReturn();
        Map<String, String> expectedErrorMap = new TreeMap<>();
        expectedErrorMap.put("category", "Banner category cannot be null");
        expectedErrorMap.put("content", "Banner content cannot be null");
        expectedErrorMap.put("name", "Banner name must be from 1 to 255 symbols length");
        expectedErrorMap.put("price", "Banner price must be positive or zero value");
        String expectedErrorString = objectMapper.writeValueAsString(expectedErrorMap);

        assertThat(postResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expectedErrorString);
        assertThat(putResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expectedErrorString);
        verify(service, times(0)).saveBanner(any());
    }

    @Test
    public void whenSaveViolatingBanner_thenReturnHTTPConflictWithErrorMessage() throws Exception {
        when(service.saveBanner(any())).thenReturn(Optional.of("ERROR"));

        mockMvc.perform(post("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("ERROR"));
        mockMvc.perform(put("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("ERROR"));
        verify(service, times(2)).saveBanner(any());
    }

    @Test
    public void whenSaveCorrectCategory_thenReturnHTTPOkWithOkMessage() throws Exception {
        when(service.saveBanner(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b1)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        mockMvc.perform(put("/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b1)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        verify(service, times(2)).saveBanner(any());
    }
}
