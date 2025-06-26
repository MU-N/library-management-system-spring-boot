package com.nasser.library.controller;

import com.nasser.library.model.entity.Book;
import com.nasser.library.service.BookService;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
@Slf4j
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    /*@GetMapping()
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir ) {

        log.info("Retrieving books - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);
        try {

            // Create pageable with validation (consolidates all pagination logic)
            Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
            if (pageable == null) {
                return ResponseEntity.badRequest().build();
            }

            // Get paginated Books
            Page<Book> books = bookService.getAllBooks(pageable);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error retrieving books: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }*/


}
