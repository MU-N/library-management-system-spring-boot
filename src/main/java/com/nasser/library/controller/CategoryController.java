package com.nasser.library.controller;

import com.nasser.library.model.dto.request.CategoryRequest;
import com.nasser.library.model.dto.response.CategoryResponse;
import com.nasser.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all categories with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of CategoryResponse objects
     */
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<CategoryResponse> categoryResponses = categoryService.getAllCategoriesWithPagination(page, size, sortBy, sortDir);
        return ResponseEntity.ok(categoryResponses);
    }


    /**
     * Creates a new category.
     *
     * @param request The CategoryRequest containing the category information
     * @return ResponseEntity containing the created CategoryResponse
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse categoryResponse = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id The ID of the category to retrieve
     * @return ResponseEntity containing the CategoryResponse if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponse);
    }

    /**
     * Updates an existing category by ID.
     *
     * @param id      The ID of the category to update
     * @param request The CategoryRequest containing updated information
     * @return ResponseEntity containing the updated CategoryResponse
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Partially updates an existing category by ID.
     *
     * @param id      The ID of the category to update
     * @param request The CategoryRequest containing fields to update
     * @return ResponseEntity containing the updated CategoryResponse
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> patchCategory(
            @PathVariable Integer id,
            @RequestBody CategoryRequest request) {
        CategoryResponse categoryResponse = categoryService.patchCategory(id, request);
        return ResponseEntity.ok(categoryResponse);
    }

    /**
     * Deletes a category by ID.
     *
     * @param id The ID of the category to delete
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }

    /**
     * Searches for categories by name or description.
     *
     * @param searchTerm The search term to look for
     * @param page       The page number (zero-based)
     * @param size       The number of items per page
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of CategoryResponse objects
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CategoryResponse>> searchCategories(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<CategoryResponse> results = categoryService.searchCategories(searchTerm, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }

    /**
     * Gets categories by code pattern.
     *
     * @param codePattern The code pattern to search for
     * @param page        The page number (zero-based)
     * @param size        The number of items per page
     * @param sortBy      The field to sort by
     * @param sortDir     The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of CategoryResponse objects
     */
    @GetMapping("/code/{codePattern}")
    public ResponseEntity<Page<CategoryResponse>> getCategoriesByCode(
            @PathVariable String codePattern,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Use search functionality to find categories by code
        Page<CategoryResponse> results = categoryService.searchCategories(codePattern, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }

}