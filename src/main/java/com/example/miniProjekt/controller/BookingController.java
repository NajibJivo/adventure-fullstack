package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.BookingStatus;
import com.example.miniProjekt.service.BookingService;
import com.example.miniProjekt.web.dto.BookingRequest;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingController – REST-endpoints for CRUD på bookinger.
 * Benytter DTO’er (BookingRequest/BookingResponse)
 * og delegerer al logik til BookingService.
 */
@RestController
@RequestMapping("api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    /** CREATE: 201 + Location */
    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest req) {
        BookingResponse created = service.create(req);
        URI location = URI.create("/api/bookings/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** READ by id */
    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** LIST all */
    @GetMapping
    public List<BookingResponse> list() {
        return service.list();
    }

    /**
     * SEARCH (separat endpoint):
     * GET /api/bookings/search?from=&to=&activityId=&customerId=&status=
     */
    @GetMapping("/search")
    public List<BookingResponse> search(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) BookingStatus status
    ) {
        // Service returnerer allerede DTO’er → ingen mapping i controller
        return service.search(from, to, activityId, customerId, status);
    }

    /** UPDATE (partial) */
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @RequestBody BookingRequest req) {
        return service.update(id, req);
    }

    /** CANCEL */
    @PutMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        // Service returnerer allerede DTO → ingen mapping i controller
        return service.cancel(id);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
