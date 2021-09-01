package com.github.neemogu.bannerads.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/banners")
public class BannerController {
    private final BannerService service;

    @Autowired
    public BannerController(BannerService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> addBanner(@RequestBody @Valid Banner banner) {
        banner.setId(null);
        Optional<String> error = service.saveBanner(banner);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @PutMapping(consumes = "application/json")
    public ResponseEntity<String> updateBanner(@RequestBody @Valid Banner banner) {
        Optional<String> error = service.saveBanner(banner);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBanner(
            @PathVariable("id") Integer id
    ) {
        service.deleteBanner(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Banner>> getBannersList(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        BannerFetchParameters parameters = BannerFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getBannerList(parameters));
    }

    @GetMapping("/pages")
    public ResponseEntity<Long> getBannersListPageCount(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        BannerFetchParameters parameters = BannerFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getBannerListPageCount(parameters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getSpecificBanner(
            @PathVariable Integer id
    ) {
        Optional<Banner> result = service.getSpecificBanner(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
