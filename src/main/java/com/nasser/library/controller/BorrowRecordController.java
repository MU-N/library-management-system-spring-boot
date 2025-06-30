package com.nasser.library.controller;

import com.nasser.library.model.dto.request.BorrowRecordRequest;
import com.nasser.library.model.dto.response.BorrowRecordResponse;
import com.nasser.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    /**
     * Retrieves a list of all borrow records with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of BorrowRecordResponse objects
     */
    @GetMapping
    public ResponseEntity<Page<BorrowRecordResponse>> getAllBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<BorrowRecordResponse> borrowRecords = borrowRecordService.getAllBorrowRecords(page, size, sortBy, sortDir);
        return ResponseEntity.ok(borrowRecords);
    }

    /**
     * Creates a new borrow record.
     *
     * @param request The BorrowRecordRequest containing the borrow record information
     * @return ResponseEntity containing the created BorrowRecordResponse if successful, or an error response
     */
    @PostMapping
    public ResponseEntity<BorrowRecordResponse> createBorrowRecord(@Valid @RequestBody BorrowRecordRequest request) {
        BorrowRecordResponse borrowRecord = borrowRecordService.createBorrowRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecord);
    }

    /**
     * Retrieves a borrow record by its ID.
     *
     * @param id The ID of the borrow record to retrieve
     * @return ResponseEntity containing the BorrowRecordResponse if found, or an error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecordResponse> getBorrowRecordById(@PathVariable Long id) {
        BorrowRecordResponse borrowRecord = borrowRecordService.getBorrowRecordById(id);
        return ResponseEntity.ok(borrowRecord);
    }

    /**
     * Updates an existing borrow record by its ID.
     *
     * @param id      The ID of the borrow record to update
     * @param request The BorrowRecordRequest containing the updated information
     * @return ResponseEntity containing the updated BorrowRecordResponse if successful, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<BorrowRecordResponse> updateBorrowRecord(
            @PathVariable Long id, 
            @Valid @RequestBody BorrowRecordRequest request) {

        BorrowRecordResponse updatedRecord = borrowRecordService.updateBorrowRecord(id, request);
        return ResponseEntity.ok(updatedRecord);
    }



    /**
     * Deletes a borrow record by its ID.
     *
     * @param id The ID of the borrow record to delete
     * @return ResponseEntity with a success message or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBorrowRecord(@PathVariable Long id) {
        borrowRecordService.deleteBorrowRecord(id);

        return ResponseEntity.ok(
                Collections.singletonMap("message", "Borrow record with ID " + id + " successfully deleted"));
    }

    /**
     * Retrieves borrow records for a specific user.
     *
     * @param userId  The ID of the user
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of BorrowRecordResponse objects
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BorrowRecordResponse>> getBorrowRecordsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<BorrowRecordResponse> borrowRecords = borrowRecordService.getBorrowRecordsByUser(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(borrowRecords);
    }



    /**
     * Retrieves overdue borrow records.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of overdue BorrowRecordResponse objects
     */
    @GetMapping("/overdue")
    public ResponseEntity<Page<BorrowRecordResponse>> getOverdueBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<BorrowRecordResponse> overdueRecords = borrowRecordService.getOverdueBorrowRecords(page, size, sortBy, sortDir);
        return ResponseEntity.ok(overdueRecords);
    }

    /**
     * Returns a book (marks borrow record as returned).
     *
     * @param id The ID of the borrow record
     * @return ResponseEntity containing the updated BorrowRecordResponse
     */
    @PatchMapping("/{id}/return")
    public ResponseEntity<BorrowRecordResponse> returnBook(@PathVariable Long id) {
        BorrowRecordResponse returnedRecord = borrowRecordService.returnBook(id);

        return ResponseEntity.ok(returnedRecord);
    }


} 