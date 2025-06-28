package com.nasser.library.controller;

import com.nasser.library.model.dto.request.AuthorRequest;
import com.nasser.library.model.dto.response.AuthorResponse;
import com.nasser.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authors")
@Slf4j
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Retrieves all authors with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of AuthorResponse objects
     */
    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<AuthorResponse> authorResponses = authorService.getAllAuthorsWithPagination(page, size, sortBy, sortDir);
        return ResponseEntity.ok(authorResponses);
    }

    /**
     * Creates a new author.
     *
     * @param request The AuthorRequest containing the author information
     * @return ResponseEntity containing the created AuthorResponse
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse authorResponse = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorResponse);
    }

    /**
     * Retrieves an author by ID.
     *
     * @param id The ID of the author to retrieve
     * @return ResponseEntity containing the AuthorResponse if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        AuthorResponse authorResponse = authorService.getAuthorById(id);
        return ResponseEntity.ok(authorResponse);
    }

    /**
     * Updates an existing author by ID.
     *
     * @param id      The ID of the author to update
     * @param request The AuthorRequest containing updated information
     * @return ResponseEntity containing the updated AuthorResponse
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        AuthorResponse updatedAuthor = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(updatedAuthor);
    }

    /**
     * Partially updates an existing author by ID.
     *
     * @param id      The ID of the author to update
     * @param request The AuthorRequest containing fields to update
     * @return ResponseEntity containing the updated AuthorResponse
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AuthorResponse> patchAuthor(
            @PathVariable Long id,
            @RequestBody AuthorRequest request) {
        AuthorResponse authorResponse = authorService.patchAuthor(id, request);
        return ResponseEntity.ok(authorResponse);
    }

    /**
     * Deletes an author by ID.
     *
     * @param id The ID of the author to delete
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(Map.of("message", "Author deleted successfully"));
    }

    /**
     * Searches for authors by name, pen name, or other criteria.
     *
     * @param searchTerm The search term to look for
     * @param page       The page number (zero-based)
     * @param size       The number of items per page
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of AuthorResponse objects
     */
    @GetMapping("/search")
    public ResponseEntity<Page<AuthorResponse>> searchAuthors(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<AuthorResponse> results = authorService.searchAuthors(searchTerm, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }

    /**
     * Gets authors by nationality.
     *
     * @param nationality The nationality to filter by
     * @param page        The page number (zero-based)
     * @param size        The number of items per page
     * @param sortBy      The field to sort by
     * @param sortDir     The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of AuthorResponse objects
     */
    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<Page<AuthorResponse>> getAuthorsByNationality(
            @PathVariable String nationality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // This would require adding a method to AuthorRepository and AuthorService
        // For now, we can use the search functionality
        Page<AuthorResponse> results = authorService.searchAuthors(nationality, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }
}