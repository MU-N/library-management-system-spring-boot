package com.nasser.library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "categories")
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must contain only uppercase letters, numbers, and underscores")
    @Column(name = "code", length = 50, unique = true)
    private String code;


    @Min(value = 0, message = "Book count cannot be negative")
    @Column(name = "book_count")
    @Builder.Default
    private Integer bookCount = 0;

    // Relationship with books
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Book> books = new HashSet<>();

    // Helper methods for relationship management
    public void addBook(Book book) {
        this.books.add(book);
        book.getCategories().add(this);
        updateBookCount();
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.getCategories().remove(this);
        updateBookCount();
    }

    // Business logic methods
    public void updateBookCount() {
        this.bookCount = this.books.size();
    }

    // Generate code from name if not provided
    @PrePersist
    @PreUpdate
    private void generateCodeIfMissing() {
        if (this.code == null || this.code.trim().isEmpty()) {
            this.code = this.name.toUpperCase()
                    .replaceAll("[^A-Z0-9]", "_")
                    .replaceAll("_{2,}", "_")
                    .replaceAll("^_|_$", "");
        }
    }
}
