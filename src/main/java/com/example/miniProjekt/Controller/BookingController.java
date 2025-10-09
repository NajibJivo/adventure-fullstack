package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.BookingService;
import com.example.miniProjekt.web.dto.BookingRequest;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    /** CREATE: POST /api/bookings – Opret ny booking (201 Created + response-DTO).  **/
    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest req) {
        BookingResponse created = service.create(req);
        // Returnerer 201 Created med Location-header pegende på den nye ressource.
        URI location = URI.create("/api/bookings/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** READ by id: GET /api/bookings/{id} – Hent booking via id (404 hvis ikke fundet). **/
    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** READ All: GET /api/bookings – Returnér alle bookinger som DTO’er.  **/
    @GetMapping
    public List<BookingResponse> list() {
        return service.list();
    }

    /** UPDATE: PUT /api/bookings/{id} – Opdater booking (partial update: null felter ignoreres). **/
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @RequestBody BookingRequest req) {
        return service.update(id, req);
    }

    /** DELETE /api/bookings/{id} – Slet booking (204 No Content). **/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
