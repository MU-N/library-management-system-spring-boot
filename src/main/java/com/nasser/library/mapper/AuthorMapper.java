package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.AuthorRequest;
import com.nasser.library.model.dto.response.AuthorResponse;
import com.nasser.library.model.entity.Author;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {


    // Request to Entity (for creation)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullName", expression = "java(request.firstName() + \" \" + request.lastName())")
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", constant = "0")
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "profileImageData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Author toEntity(AuthorRequest request);

    // Entity to Response
    @Mapping(target = "displayName", expression = "java(author.getDisplayName())")
    AuthorResponse toResponse(Author author);

    // Update existing entity from request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullName", expression = "java(request.firstName() + \" \" + request.lastName())")
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "profileImageData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AuthorRequest request, @MappingTarget Author author);

    // List mapping
    List<AuthorResponse> toResponseList(List<Author> authors);
}
