package com.nasser.library.repository;

import com.nasser.library.model.entity.Author;
import com.nasser.library.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {


    /**
     * Finds a page of authors whose books are in the specified set of books.
     *
     * @param books the set of books to search for authors
     * @param pageable the pagination information
     * @return a page of authors who have at least one book in the specified set
     */
    Page<Author> findByBooksIn(Set<Book> books, Pageable pageable);

    /**
     * Finds a page of authors whose books contain the specified book.
     *
     * @param book the book to search for authors
     * @param pageable the pagination information
     * @return a page of authors who have the specified book in their books
     */
    Page<Author> findByBooksContaining(Book book, Pageable pageable);


    /**
     * Searches for authors where firstName, lastName, fullName, or penName matches the search text.
     *
     * @param searchText the text to search for (case-insensitive)
     * @param pageable the pagination and sorting information
     * @return a page of matching authors
     */
    @Query("SELECT a FROM Author a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(a.penName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Author> searchAuthors(@Param("searchText") String searchText, Pageable pageable);


}
