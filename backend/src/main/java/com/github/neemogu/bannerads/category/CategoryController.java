package com.github.neemogu.bannerads.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
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
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @PutMapping(consumes = "application/json")
    public ResponseEntity<String> updateCategory(@RequestBody @Valid Category category) {
        Optional<String> error = service.saveCategory(category);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable("id") Integer id
    ) {
        Optional<String> error = service.deleteCategory(id);
        if (error.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(error.get(), HttpStatus.CONFLICT);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getCategoriesList(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > 100) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CategoryFetchParameters parameters = CategoryFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getCategoryList(parameters));
    }

    @GetMapping("/pages")
    public ResponseEntity<Long> getCategoriesListPageCount(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > 100) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CategoryFetchParameters parameters = CategoryFetchParameters.builder()
                .searchName(searchName)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getCategoryListPageCount(parameters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getSpecificCategory(
            @PathVariable Integer id
    ) {
        Optional<Category> result = service.getSpecificCategory(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}