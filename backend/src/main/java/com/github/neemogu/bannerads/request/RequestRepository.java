package com.github.neemogu.bannerads.request;

import com.github.neemogu.bannerads.banner.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    void deleteAllByDateBefore(Date date);
    boolean existsByBannerAndIpAddressAndUserAgentAndDateAfter(
            Banner banner, String ipAddress, String userAgent, Date date
    );
}
