package com.nasser.library.repository;

import com.nasser.library.model.entity.Book;
import com.nasser.library.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Finds categories associated with any of the provided books, with pagination and sorting.
     *
     * @param books    the set of books to search by
     * @param pageable the pagination and sorting information
     * @return a page of categories linked to any of the provided books
     */
    Page<Category> findByBooksIn(Set<Book> books, Pageable pageable);

    /**
     * Finds categories associated with a specific book, with pagination and sorting.
     *
     * @param book     the book to search by
     * @param pageable the pagination and sorting information
     * @return a page of categories linked to the provided book
     */
    Page<Category> findByBooksContaining(Book book, Pageable pageable);


    /**
     * Searches for categories where name or description matches the search text.
     *
     * @param searchText the text to search for (case-insensitive)
     * @param pageable   the pagination and sorting information
     * @return a page of matching categories
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Category> searchCategories(@Param("searchText") String searchText, Pageable pageable);
}

