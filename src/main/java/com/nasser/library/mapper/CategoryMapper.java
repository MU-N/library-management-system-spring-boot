package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.CategoryRequest;
import com.nasser.library.model.dto.response.CategoryResponse;
import com.nasser.library.model.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Request to Entity (for creation)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequest request);

    // Entity to Response
    CategoryResponse toResponse(Category category);

    // Update existing entity from request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);

    // List mapping
    List<CategoryResponse> toResponseList(List<Category> categories);
}