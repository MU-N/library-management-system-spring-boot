package com.nasser.library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "books")
public class Book extends BaseEntity {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    //987-654-321-0
    //111-222-333-X
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{3}-[0-9X]$", message = "Invalid ISBN-10 format, must be XXX-XXX-XXX-X")
    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(name = "description", length = 2000)
    private String description;

    @NotBlank(message = "Publisher is required")
    @Size(max = 200, message = "Publisher name must not exceed 200 characters")
    @Column(name = "publisher", nullable = false, length = 200)
    private String publisher;

    @NotNull(message = "Publication year is required")
    @PastOrPresent(message = "Publication year must be in the past or present")
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @NotNull(message = "Number of pages is required")
    @Min(value = 1, message = "Number of pages must be at least 1")
    @Max(value = 10000, message = "Number of pages must not exceed 10000")
    @Column(name = "pages", nullable = false)
    private Integer pages;

    @Size(max = 10, message = "Language code must not exceed 10 characters")
    @Column(name = "language", length = 10)
    private String language = "EN";

    @Size(max = 50, message = "Edition must not exceed 50 characters")
    @Column(name = "edition", length = 50)
    private String edition;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "cover_image_data")
    @ToString.Exclude
    private byte[] coverImageData;

    @NotNull(message = "Book status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookStatus status = BookStatus.AVAILABLE;

    @Min(value = 0, message = "Total copies cannot be negative")
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies = 0;

    @Min(value = 0, message = "Available copies cannot be negative")
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies = 0;

    @Column(name = "location_shelf", length = 50)
    private String locationShelf;

    @Column(name = "location_section", length = 50)
    private String locationSection;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Min(value = 0, message = "Rating count cannot be negative")
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();


    // Helper methods for relationship management
    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBooks().remove(this);
    }


    // Business logic methods
    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE && this.availableCopies > 0;
    }

    public String getFullLocation() {
        if (locationSection != null && locationShelf != null) {
            return locationSection + " - " + locationShelf;
        } else if (locationSection != null) {
            return locationSection;
        } else if (locationShelf != null) {
            return locationShelf;
        }
        return "Location not specified";
    }


}

