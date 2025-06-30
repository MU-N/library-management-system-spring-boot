package com.nasser.library.mapper;

import com.nasser.library.model.dto.request.BorrowRecordRequest;
import com.nasser.library.model.dto.response.BorrowRecordResponse;
import com.nasser.library.model.entity.BorrowRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class, FineMapper.class})
public interface BorrowRecordMapper {

    @Mapping(target = "isOverdue", expression = "java(borrowRecord.isOverdue())")
    @Mapping(target = "daysOverdue", expression = "java(borrowRecord.getDaysOverdue())")
    @Mapping(target = "isActive", expression = "java(borrowRecord.getStatus() == com.nasser.library.model.entity.BorrowStatus.ACTIVE)")
    @Mapping(target = "isReturned", expression = "java(borrowRecord.getStatus() == com.nasser.library.model.entity.BorrowStatus.RETURNED)")
    @Mapping(target = "totalFinesAmount", expression = "java(borrowRecord.getFines().stream().map(fine -> fine.getAmount()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))")
    @Mapping(target = "activeFinesCount", expression = "java((int) borrowRecord.getFines().stream().filter(fine -> fine.getStatus() == com.nasser.library.model.entity.FineStatus.PENDING).count())")
    BorrowRecordResponse toResponse(BorrowRecord borrowRecord);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "fines", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BorrowRecord toEntity(BorrowRecordRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "fines", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(BorrowRecordRequest request, @MappingTarget BorrowRecord borrowRecord);
}