package com.github.neemogu.bannerads.request;

import com.github.neemogu.bannerads.banner.Banner;
import com.github.neemogu.bannerads.banner.BannerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestService {
    private static final long millisInDay = 86400000L;
    private final RequestRepository requestRepository;
    private final BannerRepository bannerRepository;

    public RequestService(RequestRepository requestRepository, BannerRepository bannerRepository) {
        this.requestRepository = requestRepository;
        this.bannerRepository = bannerRepository;
    }

    /**
     * Selects next banner of a given category for given user agent and IP address
     * and returns it's content
     *
     * @param userAgent User agent string
     * @param ipAddress IP address string
     * @param categoryReqName Category request name to choose next banner of
     *
     * @return Banner content
     */

    public String getNextBannerContent(String userAgent, String ipAddress, String categoryReqName) {
        //TODO: implement selecting a banner
        Banner nextBanner = new Banner();
        addRequest(userAgent, ipAddress, nextBanner.getId());
        return nextBanner.getContent();
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

    /**
     * Scheduled task that removes expired requests (older than one day)
     */

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = millisInDay)
    public void deleteExpiredRequests() {
        Date yesterday = new Date(System.currentTimeMillis() - millisInDay);
        requestRepository.deleteAllByDateBefore(yesterday);
    }
}
