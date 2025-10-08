package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.BookingService;
import com.example.miniProjekt.web.dto.BookingRequest;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    /** CREATE **/
    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest req) {
        BookingResponse created = service.create(req);
        URI location = URI.create("/api/bookings/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** READ by id **/
    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** READ all **/
    @GetMapping
    public List<BookingResponse> list() {
        return service.list();
    }

    /** UPDATE (partial) **/
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @RequestBody BookingRequest req) {
        return service.update(id, req);
    }

    /** DELETE **/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
