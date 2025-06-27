package com.nasser.library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "author")
public class Author extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Size(max = 100, message = "Pen name must not exceed 100 characters")
    @Column(name = "pen_name", length = 100, unique = true)
    private String penName;

    @Size(max = 3000, message = "Biography must not exceed 3000 characters")
    @Column(name = "biography", length = 3000)
    private String biography;

    @Email(message = "Email should be valid")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    @Column(name = "email", length = 200)
    private String email;

    @Size(max = 100, message = "Nationality must not exceed 100 characters")
    @Column(name = "nationality", length = 100)
    private String nationality;

    @Past(message = "Birth date must be in the past")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Past(message = "Death date must be in the past")
    @Column(name = "death_date")
    private LocalDate deathDate;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_image_data")
    private byte[] profileImageData;

    @Column(name = "birth_place", length = 200)
    private String birthPlace;

    @Min(value = 0, message = "Book count cannot be negative")
    @Column(name = "book_count")
    private Integer bookCount = 0;

    @DecimalMin(value = "0.0", message = "Average rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Average rating cannot exceed 5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    // Relationships
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();

    // Helper methods for relationship management
    public void addBook(Book book) {
        this.books.add(book);
        book.getAuthors().add(this);
        updateBookCount();
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.getAuthors().remove(this);
        updateBookCount();
    }

    // Business logic methods
    public void updateBookCount() {
        this.bookCount = this.books.size();
    }

    public String getDisplayName() {
        return penName != null && !penName.trim().isEmpty() ? penName : fullName;
    }
}
