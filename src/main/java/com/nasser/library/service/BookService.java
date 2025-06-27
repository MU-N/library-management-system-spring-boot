package com.nasser.library.service;

import com.nasser.library.mapper.BookMapper;
import com.nasser.library.model.dto.request.BookRequest;
import com.nasser.library.model.dto.response.BookResponse;
import com.nasser.library.model.entity.Book;
import com.nasser.library.repository.BookRepository;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing book-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;


    /**
     * Retrieves all books with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of BookResponse objects
     */
    public Page<BookResponse> getAllBooksWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Retrieving books - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000; // Prevent memory issues
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Book> books = bookRepository.findAll(pageable);

            Page<BookResponse> bookResponses = books.map(bookMapper::toResponse);

            logPaginationResults(bookResponses);
            return bookResponses;
        } catch (Exception e) {
            log.error("Error retrieving books: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve books", e);
        }
    }

    /**
     * Creates a new book.
     *
     * @param request The BookRequest containing the book information.
     * @return The created book entity.
     */
    public BookResponse createBook(BookRequest request) {
        log.debug("Creating a new book: {}", request);
        try {
            Book book = bookMapper.toEntity(request);
            Book savedBook = bookRepository.save(book);
            log.debug("Successfully created a new book with ID: {}", book.getId());
            return bookMapper.toResponse(savedBook);
        } catch (Exception e) {
            log.error("Error creating a new book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create a new book", e);
        }
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id The ID of the book to retrieve.
     * @return The book entity if found.
     * @throws IllegalArgumentException if the book is not found.
     */

    public BookResponse getBookById(Long id) {
        log.debug("Retrieving Book by ID: {}", id);
        try {
            Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("book not found with ID: " + id));
            log.debug("Successfully retrieved book by ID: {}", id);
            return bookMapper.toResponse(book);
        } catch (Exception e) {
            log.error("Error retrieving book by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve book by ID", e);
        }
    }


    /**
     * Updates a book by its ID.
     *
     * @param id      The ID of the book to update.
     * @param request The Book entity containing the updated information.
     * @return The updated book entity.
     * @throws IllegalArgumentException if the book is not found.
     */
    public BookResponse updateBook(Long id, BookRequest request) {
        log.debug("Updating book with ID: {}", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("book not found with ID: " + id));
        try {
            bookMapper.updateEntityFromRequest(request, book);
            bookRepository.save(book);
            log.debug("Successfully updated book with ID: {}", id);
            return bookMapper.toResponse(book);
        } catch (Exception e) {
            log.error("Error updating book with ID: {}", id, e);
            throw new RuntimeException("Failed to update book", e);
        }
    }


    /**
     * Partially updates a book by its ID.
     *
     * @param id      The ID of the book to update.
     * @param request The BookRequest containing the fields to update.
     * @return The updated book entity.
     */
    public BookResponse patchBook(Long id, BookRequest request) {
        log.debug("Patching book with ID: {}", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("book not found with ID: " + id));
        try {
            bookMapper.updateEntityFromRequest(request, book);
            Book updatedBook = bookRepository.save(book);
            log.debug("Successfully patched book with ID: {}", id);
            return bookMapper.toResponse(updatedBook);
        } catch (Exception e) {
            log.error("Error patching book with ID: {}", id, e);
            throw new RuntimeException("Failed to patch book", e);
        }
    }


    /**
     * Deletes a book by its ID.
     *
     * @param id The ID of the book to delete.
     * @throws IllegalArgumentException if the book is not found.
     */
    public void deleteBook(Long id) {
        log.debug("Deleting book with ID: {}", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("book not found with ID: " + id));
        try {
            bookRepository.delete(book);
            log.debug("Successfully deleted book with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting book with ID: {}", id, e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    /**
     * Retrieves books by title with pagination and sorting.
     *
     * @param title   The title to search for
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of BookResponse objects
     */
    public Page<BookResponse> getBooksByTitle(String title, int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving books by title: {} - Page: {}, Size: {}, Sort: {}", title, page, size, sortBy);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000; // Prevent memory issues
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {

            Page<Book> bookPage = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
            Page<BookResponse> bookResponses = bookPage.map(bookMapper::toResponse);
            log.debug("Successfully retrieved {} books out of {} total books", bookPage.getNumberOfElements(), bookPage.getTotalElements());
            return bookResponses;
        } catch (Exception e) {
            log.error("Error retrieving books by title: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve books by title", e);
        }
    }

    /**
     * Searches for books by any similar with pagination and sorting.
     *
     * @param searchTerm The search term to search for
     * @param page       The page number (zero-based)
     * @param size       The number of items per page
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction (asc or desc)
     * @return A Page of BookResponse objects
     */
    public Page<BookResponse> searchBooks(String searchTerm, int page, int size, String sortBy, String sortDir) {

        log.info("Searching books with searchTerm: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null;
        }
        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            return null;
        }
        try {
            Page<Book> bookPage = bookRepository.searchBooks(searchTerm, pageable);
            Page<BookResponse> bookResponses = bookPage.map(bookMapper::toResponse);
            logPaginationResults(bookResponses);
            return bookResponses;
        } catch (Exception e) {
            log.error("Error searching books: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search books", e);
        }
    }


    private void logPaginationResults(Page<BookResponse> bookResponses) {
        log.info("Successfully retrieved {} books out of {} total books. Page {}/{}", bookResponses.getNumberOfElements(), bookResponses.getTotalElements(), bookResponses.getNumber() + 1, bookResponses.getTotalPages());
    }


}
