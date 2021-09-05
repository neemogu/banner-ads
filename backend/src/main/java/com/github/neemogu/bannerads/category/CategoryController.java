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

    /**
     * Maximum size of a page
     */
    public final static int maxPageSize = 100;

    /**
     * Validates and creates a new category given in json object.
     *
     * @param category Category object given in json in request body.
     * @return HTTP 409 if there was an error (uniqueness violation).
     * HTTP 400 with errors object if there was a validation error.
     * HTTP 200 with ok message if ok.
     */

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> addCategory(@RequestBody @Valid Category category) {
        category.setId(null);
        Optional<String> error = service.saveCategory(category);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    /**
     * Validates and updates an existing category given in json object.
     *
     * @param category Category object given in json in request body.
     * @return HTTP 409 if there was an error (uniqueness violation or not existing id).
     * HTTP 400 with errors object if there was a validation error.
     * HTTP 200 with ok message if ok.
     */

    @PutMapping(consumes = "application/json")
    public ResponseEntity<String> updateCategory(@RequestBody @Valid Category category) {
        Optional<String> error = service.saveCategory(category);
        return error
                .map(s -> new ResponseEntity<>(s, HttpStatus.CONFLICT))
                .orElseGet(() -> ResponseEntity.ok("OK"));
    }

    /**
     * Deletes a category with given id.
     *
     * @param id Path variable - id of a category to delete.
     * @return HTTP 204 if ok.
     * HTTP 209 with error message (containing banners IDs) if given category has any banners left.
     */

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

    /**
     * Returns paged list of categories containing search string in a name.
     * If search string is empty then returns paged list of all categories.
     *
     * @param searchName String to search in category name.
     * @param page Number of a page to return.
     * @param pageSize Size of a page to return.
     * @return HTTP 400 if page size is too much.
     * HTTP 200 with a paged list of categories found if ok.
     */

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getCategoriesList(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > maxPageSize) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CategoryFetchParameters parameters = CategoryFetchParameters.builder()
                .searchName(searchName)
                .page(page)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getCategoryList(parameters));
    }

    /**
     * Returns a number of pages of categories containing search string in a name.
     * If search string is empty then returns number of pages of all categories.
     *
     * @param searchName String to search in category name.
     * @param pageSize Page size.
     * @return HTTP 400 if page size is too much.
     * HTTP 200 with a number of pages found if ok.
     */

    @GetMapping("/pages")
    public ResponseEntity<Long> getCategoriesListPageCount(
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        if (pageSize > maxPageSize) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CategoryFetchParameters parameters = CategoryFetchParameters.builder()
                .searchName(searchName)
                .pageSize(pageSize)
                .build();
        return ResponseEntity.ok(service.getCategoryListPageCount(parameters));
    }

    /**
     * Returns a category with given id
     *
     * @param id Category id
     * @return HTTP 404 if there is no category with such id
     * HTTP 200 with a category object if ok
     */

    @GetMapping("/{id}")
    public ResponseEntity<Category> getSpecificCategory(
            @PathVariable Integer id
    ) {
        Optional<Category> result = service.getSpecificCategory(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
