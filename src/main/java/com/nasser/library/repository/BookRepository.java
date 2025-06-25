package com.nasser.library.repository;

import com.nasser.library.model.entity.Author;
import com.nasser.library.model.entity.Book;
import com.nasser.library.model.entity.Category;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    /**
     * Finds a pageable list of books that belong to any of the given categories.
     *
     * @param categories the set of categories to filter books by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of books that match the provided categories
     */
    Page<Book> findByCategoriesIn(Set<Category> categories, Pageable pageable);


    /**
     * Finds a pageable list of books that belong to the specified category.
     *
     * @param category the category to filter books by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of books that match the provided category
     */
    Page<Book> findByCategoriesContaining(Category category, Pageable pageable);


    /**
     * Finds a pageable list of books that have any of the given authors.
     *
     * @param authors the set of authors to filter books by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of books that match the provided authors
     */
    Page<Book> findByAuthorsIn(Set<Author> authors, Pageable pageable);

    /**
     * Finds a pageable list of books that have the specified author.
     *
     * @param author the author to filter books by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of books that match the provided author
     */
    Page<Book> findByAuthorsContaining(Author author, Pageable pageable);

    /**
     * Searches for books where title, ISBN, or description matches the search text.
     *
     * @param searchText the text to search for (case-insensitive)
     * @param pageable the pagination and sorting information
     * @return a page of matching books
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Book> searchBooks(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Searches for books where publisher matches the search text.
     *
     * @param searchText the text to search for (case-insensitive)
     * @param pageable the pagination and sorting information
     * @return a page of books with matching publishers
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.publisher) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Book> searchByPublisher(@Param("searchText") String searchText, Pageable pageable);


}
