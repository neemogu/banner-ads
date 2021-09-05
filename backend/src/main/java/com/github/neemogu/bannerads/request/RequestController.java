package com.github.neemogu.bannerads.request;

import com.github.neemogu.bannerads.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/bid")
public class RequestController {
    private final RequestService service;

    @Autowired
    public RequestController(RequestService service) {
        this.service = service;
    }

    /**
     * Endpoint to get content of next most priced banner for today.
     * Returns only unique banners for one IP and user agent for one day.
     *
     * @param categoryReqName Request parameter - category request name to get next banner from.
     * @param request To get IP address and User-Agent header.
     * @return HTTP 400 with error message if no category with such request name exists.
     * HTTP 409 if no banners for such IP and user agent left today.
     * HTTP 200 with next banner content if ok.
     */
    @GetMapping
    public ResponseEntity<String> getNextBanner(
            @RequestParam(name = "category") String categoryReqName,
            HttpServletRequest request
    ) {
        Optional<String> nextBannerContent;
        try {
            nextBannerContent = service.getNextBannerContent(
                    request.getHeader("User-Agent"),
                    request.getRemoteAddr(),
                    categoryReqName);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return nextBannerContent
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
