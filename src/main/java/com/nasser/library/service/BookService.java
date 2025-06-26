package com.nasser.library.service;

import com.nasser.library.model.entity.Book;
import com.nasser.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    /*public ResponseEntity<Page<List<Book>>> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok();
    }*/
}
