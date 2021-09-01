package com.github.neemogu.bannerads.request;

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

    @GetMapping
    public ResponseEntity<String> getNextBanner(
            @RequestParam(name = "category") String categoryReqName,
            HttpServletRequest request
    ) {
        Optional<String> nextBannerContent = service.getNextBannerContent(
                request.getHeader("User-Agent"),
                request.getRemoteAddr() + ":" + request.getRemotePort(),
                categoryReqName);
        return nextBannerContent
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
