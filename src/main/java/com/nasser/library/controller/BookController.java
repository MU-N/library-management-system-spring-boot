package com.nasser.library.controller;

import com.nasser.library.model.dto.request.BookRequest;
import com.nasser.library.model.dto.response.BookResponse;
import com.nasser.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
@Slf4j
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * Retrieves a list of all books with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of BookResponse objects
     */
    @GetMapping()
    public ResponseEntity<Page<BookResponse>> getAllBooks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {

        Page<BookResponse> bookResponses = bookService.getAllBooksWithPagination(page, size, sortBy, sortDir);
        return ResponseEntity.ok(bookResponses);
    }

    /**
     * Creates a new book.
     *
     * @param request The BookRequest containing the book information
     * @return ResponseEntity containing the created BookResponse if successful, or an error response
     */
    @PostMapping("")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse bookResponse = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }


    /**
     * Retrieves a book by its ID.
     *
     * @param id The ID of the book to retrieve
     * @return ResponseEntity containing the Book entity if found, or an error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBookById(id);
        return ResponseEntity.ok(bookResponse);

    }

    // update book by id

    /**
     * Updates an existing book by its ID.
     *
     * @param id      The ID of the book to update
     * @param request The Book entity containing the updated information
     * @return ResponseEntity with a success message or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }


    /**
     * Partially updates an existing book by its ID.
     *
     * @param id      The ID of the book to update
     * @param request The BookRequest containing the fields to update
     * @return ResponseEntity containing the updated BookResponse if successful, or an error response
     */
    @PatchMapping("{id}")
    public ResponseEntity<BookResponse> patchBook(@PathVariable Long id, @RequestBody BookRequest request) {
        BookResponse bookResponse = bookService.patchBook(id, request);
        return ResponseEntity.ok(bookResponse);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id The ID of the book to delete
     * @return ResponseEntity with a success message or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.ok().build();

    }

    /**
     * Retrieves a list of books by title with pagination and sorting.
     *
     * @param title   The title to search for
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of BookResponse objects
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<Page<BookResponse>> getBooksByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<BookResponse> bookResponses = bookService.getBooksByTitle(title, page, size, sortBy, sortDir);
        return ResponseEntity.ok(bookResponses);

    }


    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(@RequestParam(value = "searchTerm", required = false) String searchTerm, @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "asc") String sortDir) {

        Page<BookResponse> results = bookService.searchBooks(searchTerm, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }

}
