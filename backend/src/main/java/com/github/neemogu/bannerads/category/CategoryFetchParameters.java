package com.github.neemogu.bannerads.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryFetchParameters {
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int pageSize = 20;
    @Builder.Default
    private String searchName = "";
}
