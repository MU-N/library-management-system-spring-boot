package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.BookRequest;
import com.nasser.library.model.dto.response.AuthorResponse;
import com.nasser.library.model.dto.response.BookResponse;
import com.nasser.library.model.dto.response.CategoryResponse;
import com.nasser.library.model.entity.Author;
import com.nasser.library.model.entity.Book;
import com.nasser.library.model.entity.Category;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
/**
 * Maps Book entity to various DTOs
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    // Request to Entity (for creation)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)           // Handle with authorIds
    @Mapping(target = "categories", ignore = true)        // Handle with categoryIds
    @Mapping(target = "coverImageData", ignore = true)    // Handle separately
    @Mapping(target = "status", constant = "AVAILABLE")   // Default value
    @Mapping(target = "averageRating", ignore = true)     // Calculated field
    @Mapping(target = "ratingCount", constant = "0")      // Default value
    @Mapping(target = "createdAt", ignore = true)         // JPA handles this
    @Mapping(target = "updatedAt", ignore = true)         // JPA handles this
    Book toEntity(BookRequest request);

    // Entity to Response
    @Mapping(target = "fullLocation", expression = "java(book.getFullLocation())")
    @Mapping(target = "isAvailable", expression = "java(book.isAvailable())")
    BookResponse toResponse(Book book);

    // Update existing entity from request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "coverImageData", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(BookRequest request, @MappingTarget Book user);

    // List mapping
    List<BookResponse> toResponseList(List<Book> books);

    // Author mapping
    @Mapping(target = "fullName", expression = "java(author.getFirstName() + \" \" + author.getLastName())")
    AuthorResponse toAuthorResponse(Author author);

    Set<AuthorResponse> toAuthorResponseSet(Set<Author> authors);

    // Category mapping
    CategoryResponse toCategoryResponse(Category category);

    Set<CategoryResponse> toCategoryResponseSet(Set<Category> categories);

}
