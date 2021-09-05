package com.github.neemogu.bannerads.controllers;

import com.github.neemogu.bannerads.exceptions.BadRequestException;
import com.github.neemogu.bannerads.request.RequestController;
import com.github.neemogu.bannerads.request.RequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;

    @Test
    public void whenServiceThrowsBadRequestException_shouldReturnHTTPBadRequest() throws Exception {
        when(requestService.getNextBannerContent(any(), any(), eq("music"))).thenThrow(BadRequestException.class);
        mockMvc.perform(get("/bid")
                .param("category", "music"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(1)).getNextBannerContent(any(), any(), any());
    }

    @Test
    public void whenServiceReturnsOk_shouldReturnHTTPOkWithBannerContent() throws Exception {
        when(requestService.getNextBannerContent(any(), any(), eq("music"))).thenReturn(Optional.of("CONTENT"));
        mockMvc.perform(get("/bid")
                .param("category", "music"))
                .andExpect(status().isOk())
                .andExpect(content().string("CONTENT"));
        verify(requestService, times(1)).getNextBannerContent(any(), any(), any());
    }

    @Test
    public void whenServiceReturnsNoContent_shouldReturnHTTPNoContent() throws Exception {
        when(requestService.getNextBannerContent(any(), any(), eq("music"))).thenReturn(Optional.empty());
        mockMvc.perform(get("/bid")
                .param("category", "music"))
                .andExpect(status().isNoContent());
        verify(requestService, times(1)).getNextBannerContent(any(), any(), any());
    }
}
