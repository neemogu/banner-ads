package com.github.neemogu.bannerads.banner;

import com.github.neemogu.bannerads.util.SortDirection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public final class BannerFetchParameters {
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int pageSize = 20;
    @Builder.Default
    private BannerSortBy sortBy = BannerSortBy.NAME;
    @Builder.Default
    private SortDirection sortDirection = SortDirection.NONE;
    @Builder.Default
    private String searchName = "";
}
