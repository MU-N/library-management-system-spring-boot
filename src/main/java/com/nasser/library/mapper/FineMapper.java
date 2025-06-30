package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.FineRequest;
import com.nasser.library.model.dto.response.FineResponse;
import com.nasser.library.model.dto.response.UserSummary;
import com.nasser.library.model.dto.response.BorrowRecordSummary;
import com.nasser.library.model.dto.response.BookSummary;
import com.nasser.library.model.entity.Fine;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FineMapper {

    @Mapping(target = "remainingAmount", expression = "java(fine.getRemainingAmount())")
    @Mapping(target = "isPending", expression = "java(fine.isPending())")
    @Mapping(target = "isPaid", expression = "java(fine.isPaid())")
    @Mapping(target = "isOverdue", expression = "java(fine.isOverdue())")
    @Mapping(target = "isFullyPaid", expression = "java(fine.isFullyPaid())")
    @Mapping(target = "daysSinceIssue", expression = "java(java.time.temporal.ChronoUnit.DAYS.between(fine.getIssueDate(), java.time.LocalDate.now()))")
    @Mapping(target = "daysUntilDue", expression = "java(fine.getDueDate() != null ? java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), fine.getDueDate()) : 0)")
    @Mapping(target = "user", expression = "java(mapToUserSummary(fine.getUser()))")
    @Mapping(target = "borrowRecord", expression = "java(mapToBorrowRecordSummary(fine.getBorrowRecord()))")
    @Mapping(target = "book", expression = "java(fine.getBorrowRecord() != null ? mapToBookSummary(fine.getBorrowRecord().getBook()) : null)")
    FineResponse toResponse(Fine fine);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "borrowRecord", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Fine toEntity(FineRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "borrowRecord", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FineRequest request, @MappingTarget Fine fine);

    // Helper methods for nested object mapping
    default UserSummary mapToUserSummary(com.nasser.library.model.entity.User user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getRole().name() // Using role as membership status
        );
    }

    default BorrowRecordSummary mapToBorrowRecordSummary(com.nasser.library.model.entity.BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            return null;
        }
        return new BorrowRecordSummary(
                borrowRecord.getId(),
                borrowRecord.getBorrowDate(),
                borrowRecord.getDueDate(),
                borrowRecord.getReturnDate(),
                borrowRecord.getStatus(),
                borrowRecord.isOverdue()
        );
    }

    default BookSummary mapToBookSummary(com.nasser.library.model.entity.Book book) {
        if (book == null) {
            return null;
        }
        return new BookSummary(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthors().stream()
                        .map(author -> author.getFirstName() + " " + author.getLastName())
                        .collect(java.util.stream.Collectors.toSet()),
                book.getStatus()
        );
    }
}