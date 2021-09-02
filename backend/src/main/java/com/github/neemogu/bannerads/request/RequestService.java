package com.github.neemogu.bannerads.request;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerFetchParameters;
import com.github.neemogu.bannerads.banner.BannerService;
import com.github.neemogu.bannerads.banner.BannerSortBy;
import com.github.neemogu.bannerads.category.Category;
import com.github.neemogu.bannerads.category.CategoryRepository;
import com.github.neemogu.bannerads.exceptions.BadRequestException;
import com.github.neemogu.bannerads.util.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    private static final int bannerPageSize = 10;
    private static final long millisInDay = 86400000L;
    private final RequestRepository requestRepository;
    private final BannerService bannerService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository,
                          BannerService bannerService,
                          CategoryRepository categoryRepository) {
        this.requestRepository = requestRepository;
        this.bannerService = bannerService;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Selects next banner of a given category for given user agent and IP address
     * and returns it's content.
     *
     * @param userAgent User agent string.
     * @param ipAddress IP address string.
     * @param categoryReqName Category request name to choose next banner of.
     *
     * @return Banner content.
     * @throws BadRequestException If category with such request name does not exist.
     */

    public Optional<String> getNextBannerContent(String userAgent,
                                                 String ipAddress,
                                                 String categoryReqName) throws BadRequestException {
        Optional<Category> foundCategory = categoryRepository.findByReqName(categoryReqName);
        if (foundCategory.isEmpty()) {
            throw new BadRequestException("Category with such request name does not exist");
        }
        final Date yesterday = new Date(System.currentTimeMillis() - millisInDay);
        BannerFetchParameters bannerFetchParameters = BannerFetchParameters.builder()
                .pageSize(bannerPageSize)
                .sortBy(BannerSortBy.PRICE)
                .sortDirection(SortDirection.DESC)
                .categoryId(foundCategory.get().getId())
                .build();
        long bannerPagesCount = bannerService.getBannerListPageCount(bannerFetchParameters);
        for (int i = 0; i < bannerPagesCount; ++i) {
            bannerFetchParameters.setPage(i);
            List<Banner> nextBanners = bannerService.getBannerList(bannerFetchParameters);
            for (Banner b : nextBanners) {
                if (!requestRepository.existsByBannerAndIpAddressAndUserAgentAndDateAfter(
                        b, ipAddress, userAgent, yesterday
                )) {
                    addRequest(userAgent, ipAddress, b.getId());
                    return Optional.of(b.getContent());
                }
            }
        }
        return Optional.empty();
    }

    private void addRequest(String userAgent, String ipAddress, Integer bannerId) {
        Request newRequest = new Request();
        newRequest.setId(null);
        Banner banner = new Banner();
        banner.setId(bannerId);
        newRequest.setBanner(banner);
        newRequest.setUserAgent(userAgent);
        newRequest.setIpAddress(ipAddress);
        requestRepository.save(newRequest);
    }
}
