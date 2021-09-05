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

    /**
     * Maximum size of a page
     */
    public final static int maxPageSize = 100;

    /**
     * Validates and creates a new banner given in json object.
     *
     * @param banner Banner object given in json in request body.
     * @return HTTP 409 if there was an error (uniqueness violation).
     * HTTP 400 with errors object if there was a validation error.
     * HTTP 200 with ok message if ok.
     */

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> addBanner(@RequestBody @Valid Banner banner) {
        banner.setId(null);
        Optional<String> error = service.saveBanner(banner);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    /**
     * Validates and updates an existing banner given in json object.
     *
     * @param banner Banner object given in json in request body.
     * @return HTTP 409 if there was an error (uniqueness violation or not existing id).
     * HTTP 400 with errors object if there was a validation error.
     * HTTP 200 with ok message if ok.
     */

    @PutMapping(consumes = "application/json")
    public ResponseEntity<String> updateBanner(@RequestBody @Valid Banner banner) {
        Optional<String> error = service.saveBanner(banner);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    /**
     * Deletes a banner with given id.
     *
     * @param id Path variable - id of a banner to delete.
     * @return HTTP 204.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBanner(
            @PathVariable("id") Integer id
    ) {
        service.deleteBanner(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns paged list of banners containing search string in a name.
     * If search string is empty then returns paged list of all banners.
     *
     * @param searchName String to search in banner name.
     * @param page Number of a page to return.
     * @param pageSize Size of a page to return.
     * @return HTTP 400 if page size is too much.
     * HTTP 200 with a paged list of banners found if ok.
     */

    @GetMapping("/list")
    public ResponseEntity<List<Banner>> getBannersList(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > maxPageSize) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BannerFetchParameters parameters = BannerFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getBannerList(parameters));
    }

    /**
     * Returns a number of pages of banners containing search string in a name.
     * If search string is empty then returns number of pages of all banners.
     *
     * @param searchName String to search in banner name.
     * @param pageSize Page size.
     * @return HTTP 400 if page size is too much.
     * HTTP 200 with a number of pages found if ok.
     */

    @GetMapping("/pages")
    public ResponseEntity<Long> getBannersListPageCount(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > maxPageSize) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BannerFetchParameters parameters = BannerFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getBannerListPageCount(parameters));
    }

    /**
     * Returns a banner with given id
     *
     * @param id Banner id
     * @return HTTP 404 if there is no banner with such id
     * HTTP 200 with a banner object if ok
     */

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getSpecificBanner(
            @PathVariable Integer id
    ) {
        Optional<Banner> result = service.getSpecificBanner(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
