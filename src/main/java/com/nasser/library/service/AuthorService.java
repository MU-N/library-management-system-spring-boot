package com.nasser.library.service;

import com.nasser.library.mapper.AuthorMapper;
import com.nasser.library.model.dto.request.AuthorRequest;
import com.nasser.library.model.dto.response.AuthorResponse;
import com.nasser.library.model.entity.Author;
import com.nasser.library.repository.AuthorRepository;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing author-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    /**
     * Retrieves all authors with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of AuthorResponse objects
     */
    public Page<AuthorResponse> getAllAuthorsWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Retrieving authors - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Author> authors = authorRepository.findAll(pageable);
            Page<AuthorResponse> authorResponses = authors.map(authorMapper::toResponse);

            logPaginationResults(authorResponses);
            return authorResponses;
        } catch (Exception e) {
            log.error("Error retrieving authors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve authors", e);
        }
    }

    /**
     * Creates a new author.
     *
     * @param request The AuthorRequest containing the author information
     * @return The created AuthorResponse
     */
    public AuthorResponse createAuthor(AuthorRequest request) {
        log.debug("Creating a new author: {} {}", request.firstName(), request.lastName());

        try {
            Author author = authorMapper.toEntity(request);
            Author savedAuthor = authorRepository.save(author);
            log.debug("Successfully created author with ID: {}", savedAuthor.getId());
            return authorMapper.toResponse(savedAuthor);
        } catch (Exception e) {
            log.error("Error creating author: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create author", e);
        }
    }

    /**
     * Retrieves an author by ID.
     *
     * @param id The ID of the author to retrieve
     * @return The AuthorResponse if found
     * @throws IllegalArgumentException if the author is not found
     */
    public AuthorResponse getAuthorById(Long id) {
        log.debug("Retrieving author by ID: {}", id);

        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));
            log.debug("Successfully retrieved author by ID: {}", id);
            return authorMapper.toResponse(author);
        } catch (Exception e) {
            log.error("Error retrieving author by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve author by ID", e);
        }
    }

    /**
     * Updates an author by ID.
     *
     * @param id      The ID of the author to update
     * @param request The AuthorRequest containing updated information
     * @return The updated AuthorResponse
     * @throws IllegalArgumentException if the author is not found
     */
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        log.debug("Updating author with ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));

        try {
            authorMapper.updateEntityFromRequest(request, author);
            Author updatedAuthor = authorRepository.save(author);
            log.debug("Successfully updated author with ID: {}", id);
            return authorMapper.toResponse(updatedAuthor);
        } catch (Exception e) {
            log.error("Error updating author with ID: {}", id, e);
            throw new RuntimeException("Failed to update author", e);
        }
    }

    /**
     * Partially updates an author by ID.
     *
     * @param id      The ID of the author to update
     * @param request The AuthorRequest containing fields to update
     * @return The updated AuthorResponse
     */
    public AuthorResponse patchAuthor(Long id, AuthorRequest request) {
        log.debug("Patching author with ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));

        try {
            authorMapper.updateEntityFromRequest(request, author);
            Author updatedAuthor = authorRepository.save(author);
            log.debug("Successfully patched author with ID: {}", id);
            return authorMapper.toResponse(updatedAuthor);
        } catch (Exception e) {
            log.error("Error patching author with ID: {}", id, e);
            throw new RuntimeException("Failed to patch author", e);
        }
    }

    /**
     * Deletes an author by ID.
     *
     * @param id The ID of the author to delete
     * @throws IllegalArgumentException if the author is not found
     */
    public void deleteAuthor(Long id) {
        log.debug("Deleting author with ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));

        try {
            // Check if author has books
            if (!author.getBooks().isEmpty()) {
                log.warn("Attempting to delete author with ID: {} who has {} associated books", id, author.getBooks().size());
                throw new IllegalStateException("Cannot delete author with associated books. Remove book associations first.");
            }

            authorRepository.delete(author);
            log.debug("Successfully deleted author with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting author with ID: {}", id, e);
            throw new RuntimeException("Failed to delete author", e);
        }
    }

    /**
     * Searches for authors by name, pen name, or other criteria.
     *
     * @param searchTerm The search term
     * @param page       The page number (zero-based)
     * @param size       The number of items per page
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction (asc or desc)
     * @return A Page of AuthorResponse objects
     */
    public Page<AuthorResponse> searchAuthors(String searchTerm, int page, int size, String sortBy, String sortDir) {
        log.debug("Searching authors with term: {} - Page: {}, Size: {}, Sort: {}", searchTerm, page, size, sortBy);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Author> authorPage;

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                authorPage = authorRepository.findAll(pageable);
            } else {
                authorPage = authorRepository.searchAuthors(searchTerm.trim(), pageable);
            }

            Page<AuthorResponse> authorResponses = authorPage.map(authorMapper::toResponse);
            log.debug("Successfully found {} authors out of {} total",
                    authorPage.getNumberOfElements(), authorPage.getTotalElements());
            return authorResponses;
        } catch (Exception e) {
            log.error("Error searching authors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search authors", e);
        }
    }

    private void logPaginationResults(Page<AuthorResponse> authorResponses) {
        log.debug("Retrieved {} authors out of {} total authors on page {} of {}",
                authorResponses.getNumberOfElements(),
                authorResponses.getTotalElements(),
                authorResponses.getNumber() + 1,
                authorResponses.getTotalPages());
    }
}