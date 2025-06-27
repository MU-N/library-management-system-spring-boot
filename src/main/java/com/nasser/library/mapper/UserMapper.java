package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.RegisterRequest;
import com.nasser.library.model.dto.request.UpdateUserRequest;
import com.nasser.library.model.dto.response.UserResponse;
import com.nasser.library.model.dto.response.UserProfileResponse;
import com.nasser.library.model.entity.User;
import org.mapstruct.*;

/**
 * Maps User entity to various DTOs
 */

@Mapper(componentModel = "spring")
public interface UserMapper {


    // Entity to Response DTOs
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponse toResponse(User user);


    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "totalBooksBorrowed", ignore = true)
    @Mapping(target = "currentlyBorrowed", ignore = true)
    @Mapping(target = "hasActiveFines", ignore = true)
    UserProfileResponse toProfileResponse(User user);

    @Mapping(target = "password", ignore = true) // Password handled separately
    @Mapping(target = "provider", constant = "LOCAL")
    @Mapping(target = "role", ignore = true) // Role will be set separately
    User toEntity(UpdateUserRequest request);


    @Mapping(target = "provider", constant = "LOCAL")
    @Mapping(target = "maxBooksAllowed", constant = "5")
    @Mapping(target = "role", constant = "MEMBER")
    User toEntity(RegisterRequest request);

    // Update existing entity from request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)  // Don't update password via UpdateUserRequest
    @Mapping(target = "provider", ignore = true)  // Don't change provider
    @Mapping(target = "createdAt", ignore = true) //
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);

}