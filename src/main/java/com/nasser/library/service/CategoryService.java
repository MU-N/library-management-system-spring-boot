package com.nasser.library.service;

import com.nasser.library.mapper.CategoryMapper;
import com.nasser.library.model.dto.request.CategoryRequest;
import com.nasser.library.model.dto.response.CategoryResponse;
import com.nasser.library.model.entity.Category;
import com.nasser.library.repository.CategoryRepository;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing category-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves all categories with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of CategoryResponse objects
     */
    public Page<CategoryResponse> getAllCategoriesWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Retrieving categories - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Category> categories = categoryRepository.findAll(pageable);
            Page<CategoryResponse> categoryResponses = categories.map(categoryMapper::toResponse);

            logPaginationResults(categoryResponses);
            return categoryResponses;
        } catch (Exception e) {
            log.error("Error retrieving categories: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve categories", e);
        }
    }

    /**
     * Creates a new category.
     *
     * @param request The CategoryRequest containing the category information
     * @return The created CategoryResponse
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating a new category: {}", request.name());

        // Check if category with same name already exists
        if (categoryRepository.findAll().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(request.name()))) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }

        try {
            Category category = categoryMapper.toEntity(request);
            Category savedCategory = categoryRepository.save(category);
            log.debug("Successfully created category with ID: {}", savedCategory.getId());
            return categoryMapper.toResponse(savedCategory);
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create category", e);
        }
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id The ID of the category to retrieve
     * @return The CategoryResponse if found
     * @throws IllegalArgumentException if the category is not found
     */
    public CategoryResponse getCategoryById(Integer id) {
        log.debug("Retrieving category by ID: {}", id);

        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
            log.debug("Successfully retrieved category by ID: {}", id);
            return categoryMapper.toResponse(category);
        } catch (Exception e) {
            log.error("Error retrieving category by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve category by ID", e);
        }
    }

    /**
     * Updates a category by ID.
     *
     * @param id      The ID of the category to update
     * @param request The CategoryRequest containing updated information
     * @return The updated CategoryResponse
     * @throws IllegalArgumentException if the category is not found
     */
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        log.debug("Updating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        // Check if another category with same name exists (excluding current one)
        if (categoryRepository.findAll().stream()
                .anyMatch(cat -> !cat.getId().equals(id) && cat.getName().equalsIgnoreCase(request.name()))) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }

        try {
            categoryMapper.updateEntityFromRequest(request, category);
            Category updatedCategory = categoryRepository.save(category);
            log.debug("Successfully updated category with ID: {}", id);
            return categoryMapper.toResponse(updatedCategory);
        } catch (Exception e) {
            log.error("Error updating category with ID: {}", id, e);
            throw new RuntimeException("Failed to update category", e);
        }
    }

    /**
     * Partially updates a category by ID.
     *
     * @param id      The ID of the category to update
     * @param request The CategoryRequest containing fields to update
     * @return The updated CategoryResponse
     */
    public CategoryResponse patchCategory(Integer id, CategoryRequest request) {
        log.debug("Patching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        // Check if another category with same name exists (excluding current one) - only if name is being updated
        if (request.name() != null && categoryRepository.findAll().stream()
                .anyMatch(cat -> !cat.getId().equals(id) && cat.getName().equalsIgnoreCase(request.name()))) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }

        try {
            categoryMapper.updateEntityFromRequest(request, category);
            Category updatedCategory = categoryRepository.save(category);
            log.debug("Successfully patched category with ID: {}", id);
            return categoryMapper.toResponse(updatedCategory);
        } catch (Exception e) {
            log.error("Error patching category with ID: {}", id, e);
            throw new RuntimeException("Failed to patch category", e);
        }
    }

    /**
     * Deletes a category by ID.
     *
     * @param id The ID of the category to delete
     * @throws IllegalArgumentException if the category is not found
     * @throws IllegalStateException if the category has associated books
     */
    public void deleteCategory(Integer id) {
        log.debug("Deleting category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        try {
            // Check if category has books
            if (!category.getBooks().isEmpty()) {
                log.warn("Attempting to delete category with ID: {} which has {} associated books",
                        id, category.getBooks().size());
                throw new IllegalStateException("Cannot delete category with associated books. Remove book associations first.");
            }

            categoryRepository.delete(category);
            log.debug("Successfully deleted category with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting category with ID: {}", id, e);
            throw new RuntimeException("Failed to delete category", e);
        }
    }

    /**
     * Searches for categories by name or description.
     *
     * @param searchTerm The search term
     * @param page       The page number (zero-based)
     * @param size       The number of items per page
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction (asc or desc)
     * @return A Page of CategoryResponse objects
     */
    public Page<CategoryResponse> searchCategories(String searchTerm, int page, int size, String sortBy, String sortDir) {
        log.debug("Searching categories with term: {} - Page: {}, Size: {}, Sort: {}", searchTerm, page, size, sortBy);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Category> categoryPage;

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                categoryPage = categoryRepository.findAll(pageable);
            } else {
                categoryPage = categoryRepository.searchCategories(searchTerm.trim(), pageable);
            }

            Page<CategoryResponse> categoryResponses = categoryPage.map(categoryMapper::toResponse);
            log.debug("Successfully found {} categories out of {} total",
                    categoryPage.getNumberOfElements(), categoryPage.getTotalElements());
            return categoryResponses;
        } catch (Exception e) {
            log.error("Error searching categories: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search categories", e);
        }
    }



    private void logPaginationResults(Page<CategoryResponse> categoryResponses) {
        log.debug("Retrieved {} categories out of {} total categories on page {} of {}",
                categoryResponses.getNumberOfElements(),
                categoryResponses.getTotalElements(),
                categoryResponses.getNumber() + 1,
                categoryResponses.getTotalPages());
    }
}