package com.github.neemogu.bannerads;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerFetchParameters;
import com.github.neemogu.bannerads.banner.BannerService;
import com.github.neemogu.bannerads.banner.BannerSortBy;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.exceptions.BadRequestException;
import com.github.neemogu.bannerads.request.RequestRepository;
import com.github.neemogu.bannerads.request.RequestService;
import com.github.neemogu.bannerads.util.SortDirection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BannerService bannerService;

    @InjectMocks
    private RequestService service;

    private Category category;
    private Banner b1;
    private Banner b2;
    private Banner b3;
    private Banner b4;
    private Banner b5;
    private Banner b6;

    private final String userAgent1 = "Google Chrome";
    private final String userAgent2 = "Curl";
    private final String ip1 = "192.168.0.100";
    private final String ip2 = "10.4.0.68";

    @BeforeEach
    public void init() {
        category = new Category(1, "Music", "music", false);
        b1 = new Banner(1, "ban1", 4.49, category, "BANNER_1", false);
        b2 = new Banner(2, "ban2", 2.99, category, "BANNER_2", false);
        b3 = new Banner(3, "ban3", 4.49, category, "BANNER_3", false);
    }

    @Test
    public void givenNotExistingCategoryReqName_shouldThrowBadRequestException() {
        when(categoryRepository.findByReqName(category.getReqName())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class,
                () -> service.getNextBannerContent(userAgent1, ip1, category.getReqName()));
    }

    @Test
    public void givenOneIPAndUserAgent_shouldReturnUniqueForIpAndAgentBannersContentByHighestPriceAndSaveRequests()
    throws BadRequestException {
        when(categoryRepository.findByReqName(category.getReqName())).thenReturn(Optional.of(category));
        BannerFetchParameters parameters1 = BannerFetchParameters.builder()
                .categoryId(category.getId()).sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.DESC)
                .page(0).pageSize(RequestService.bannerPageSize).build();
        BannerFetchParameters parameters2 = BannerFetchParameters.builder()
                .categoryId(category.getId()).sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.DESC)
                .page(1).pageSize(RequestService.bannerPageSize).build();

        when(bannerService.getBannerListPageCount(parameters1)).thenReturn(2L);
        when(bannerService.getBannerList(parameters1)).thenReturn(List.of(b1, b3));
        when(bannerService.getBannerList(parameters2)).thenReturn(List.of(b2));

        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b1), eq(ip1), eq(userAgent1), any()
        )).thenReturn(false, true);
        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b2), eq(ip1), eq(userAgent1), any()
        )).thenReturn(false, true);
        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b3), eq(ip1), eq(userAgent1), any()
        )).thenReturn(false, true);

        Optional<String> res1 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        assertTrue(res1.isPresent());
        assertTrue(res1.get().equals("BANNER_1") || res1.get().equals("BANNER_3"));
        Optional<String> res2 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        assertTrue(res2.isPresent());
        assertTrue(res2.get().equals("BANNER_1") || res2.get().equals("BANNER_3"));
        Optional<String> res3 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        assertTrue(res3.isPresent());
        assertEquals(res3.get(), "BANNER_2");
        Optional<String> res4 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        assertTrue(res4.isEmpty());

        verify(requestRepository, times(3)).save(any());
    }

    @Test
    public void givenDifferentIpOrUserAgent_shouldReturnSameBannerContent()
    throws BadRequestException{
        when(categoryRepository.findByReqName(category.getReqName())).thenReturn(Optional.of(category));
        BannerFetchParameters parameters1 = BannerFetchParameters.builder()
                .categoryId(category.getId()).sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.DESC)
                .page(0).pageSize(RequestService.bannerPageSize).build();

        when(bannerService.getBannerListPageCount(parameters1)).thenReturn(2L);
        when(bannerService.getBannerList(parameters1)).thenReturn(List.of(b1, b2));

        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b1), eq(ip1), eq(userAgent1), any()
        )).thenReturn(false, true);
        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b1), eq(ip2), eq(userAgent2), any()
        )).thenReturn(false, true);

        Optional<String> res1 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        Optional<String> res2 = service.getNextBannerContent(userAgent2, ip2, category.getReqName());
        assertTrue(res1.isPresent());
        assertTrue(res2.isPresent());
        assertEquals(res1.get(), res2.get());

        verify(requestRepository, times(2)).save(any());
    }

    @Test
    public void givenOneIPAndUserAgent_whenGetNextBannerAtDifferentDays_thenReturnSameBannerContent()
            throws BadRequestException{
        when(categoryRepository.findByReqName(category.getReqName())).thenReturn(Optional.of(category));
        BannerFetchParameters parameters1 = BannerFetchParameters.builder()
                .categoryId(category.getId()).sortBy(BannerSortBy.PRICE).sortDirection(SortDirection.DESC)
                .page(0).pageSize(RequestService.bannerPageSize).build();

        when(bannerService.getBannerListPageCount(parameters1)).thenReturn(2L);
        when(bannerService.getBannerList(parameters1)).thenReturn(List.of(b1, b2));

        when(requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                eq(b1), eq(ip1), eq(userAgent1), any()
        )).thenReturn(false);

        Optional<String> res1 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        // one day passed
        Optional<String> res2 = service.getNextBannerContent(userAgent1, ip1, category.getReqName());
        assertTrue(res1.isPresent());
        assertTrue(res2.isPresent());
        assertEquals(res1.get(), res2.get());

        verify(requestRepository, times(2)).save(any());
    }
}
