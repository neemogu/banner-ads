package com.github.neemogu.bannerads.category;

import com.github.neemogu.bannerads.banner.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> addCategory(@RequestBody @Valid Category category) {
        category.setId(null);
        Optional<String> error = service.saveCategory(category);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @PutMapping(consumes = "application/json")
    public ResponseEntity<String> updateCategory(@RequestBody @Valid Category category) {
        Optional<String> error = service.saveCategory(category);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<Banner>> deleteCategory(
            @PathVariable("id") Integer id
    ) {
        List<Banner> notDeletedBanners = service.deleteCategory(id);
        if (notDeletedBanners.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notDeletedBanners, HttpStatus.CONFLICT);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getCategoriesList(
            @RequestParam(name = "searchName", defaultValue = "") String searchName
    ) {
        return ResponseEntity.ok(service.getCategoryList(searchName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getSpecificCategory(
            @PathVariable Integer id
    ) {
        Optional<Category> result = service.getSpecificCategory(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
