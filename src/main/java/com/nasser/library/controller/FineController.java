package com.nasser.library.controller;

import com.nasser.library.model.dto.request.FineRequest;
import com.nasser.library.model.dto.response.FineResponse;
import com.nasser.library.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    /**
     * Retrieves a list of all fines with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of FineResponse objects
     */
    @GetMapping
    public ResponseEntity<Page<FineResponse>> getAllFines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<FineResponse> fines = fineService.getAllFines(page, size, sortBy, sortDir);
        return ResponseEntity.ok(fines);
    }

    /**
     * Creates a new fine.
     *
     * @param request The FineRequest containing the fine information
     * @return ResponseEntity containing the created FineResponse if successful, or an error response
     */
    @PostMapping
    public ResponseEntity<FineResponse> createFine(@Valid @RequestBody FineRequest request) {
        FineResponse fine = fineService.createFine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(fine);
    }

    /**
     * Retrieves a fine by its ID.
     *
     * @param id The ID of the fine to retrieve
     * @return ResponseEntity containing the FineResponse if found, or an error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<FineResponse> getFineById(@PathVariable Long id) {
        FineResponse fine = fineService.getFineById(id);
        return ResponseEntity.ok(fine);
    }

    /**
     * Updates an existing fine by its ID.
     *
     * @param id      The ID of the fine to update
     * @param request The FineRequest containing the updated information
     * @return ResponseEntity containing the updated FineResponse if successful, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<FineResponse> updateFine(
            @PathVariable Long id, 
            @Valid @RequestBody FineRequest request) {
        
        FineResponse updatedFine = fineService.updateFine(id, request);
        return ResponseEntity.ok(updatedFine);
    }

    /**
     * Deletes a fine by its ID.
     *
     * @param id The ID of the fine to delete
     * @return ResponseEntity with a success message or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFine(@PathVariable Long id) {
        fineService.deleteFine(id);
        return ResponseEntity.ok(
                Collections.singletonMap("message", "Fine with ID " + id + " successfully deleted"));
    }

    /**
     * Retrieves fines for a specific user.
     *
     * @param userId  The ID of the user
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of FineResponse objects
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FineResponse>> getFinesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<FineResponse> fines = fineService.getFinesByUser(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(fines);
    }



    /**
     * Retrieves overdue fines.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of overdue FineResponse objects
     */
    @GetMapping("/overdue")
    public ResponseEntity<Page<FineResponse>> getOverdueFines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<FineResponse> overdueFines = fineService.getOverdueFines(page, size, sortBy, sortDir);
        return ResponseEntity.ok(overdueFines);
    }

    /**
     * Processes payment for a fine.
     *
     * @param id            The ID of the fine
     * @param paymentAmount The amount being paid
     * @return ResponseEntity containing the updated FineResponse
     */
    @PatchMapping("/{id}/pay")
    public ResponseEntity<FineResponse> payFine(
            @PathVariable Long id,
            @RequestParam BigDecimal paymentAmount) {
        
        FineResponse paidFine = fineService.payFine(id, paymentAmount);
        return ResponseEntity.ok(paidFine);
    }
} 