package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.model.BookingStatus;
import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.repository.BookingRepository;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.service.exceptions.BookingNotFoundException;
import com.example.miniProjekt.web.dto.BookingRequest;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;
    private final ActivityRepository activityRepo;
    private final CustomerRepository customerRepo;

    public BookingService(BookingRepository bookingRepo,
                          ActivityRepository activityRepo,
                          CustomerRepository customerRepo) {
        this.bookingRepo = bookingRepo;
        this.activityRepo = activityRepo;
        this.customerRepo = customerRepo;
    }

    /** CREATE **/
    @Transactional
    public BookingResponse create(BookingRequest req) {
        Activity activity = activityRepo.findById(req.activityId())
                .orElseThrow(()
                        -> new IllegalArgumentException("Activity not found: " + req.activityId()));
        Customer customer = customerRepo.findById(req.customerId())
                .orElseThrow(()
                        -> new IllegalArgumentException("Customer not found: " + req.customerId()));

        Booking b = new Booking();
        b.setActivity(activity);
        b.setCustomer(customer);
        b.setStartDatetime(req.startDatetime());
        b.setParticipants(req.participants());
        b.setInstructorName(req.instructorName());
        b.setBookingStatus(req.bookingStatus() == null ? BookingStatus.EDIT : req.bookingStatus());

        return toResponse(bookingRepo.save(b));
    }


    /** READ single **/
    @Transactional(readOnly = true)
    public BookingResponse get(Long id) {
        return bookingRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    /** READ all **/
    @Transactional(readOnly = true)
    public List<BookingResponse> list() {
        return bookingRepo.findAll().stream().map(this::toResponse).toList();
    }

    /** UPDATE **/
    @Transactional
    public BookingResponse update(Long id, BookingRequest req) {
        Booking b = bookingRepo.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        if(req.activityId() != null) {
            b.setActivity(activityRepo.findById(req.activityId())
               .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId())));
        }

        if(req.customerId() != null) {
            b.setCustomer(customerRepo.findById(req.customerId())
                 .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId())));
        }
        if(req.startDatetime()!=null){b.setStartDatetime(req.startDatetime());}
        if(req.participants() != null) {b.setParticipants(req.participants());}
        if(req.instructorName() != null) {b.setInstructorName(req.instructorName());}
        if(req.bookingStatus() != null) {b.setBookingStatus(req.bookingStatus());}

        return toResponse(bookingRepo.save(b));
    }

    /** DELETE **/
    @Transactional
    public void delete(Long id) {
        if(!bookingRepo.existsById(id)) {
            throw new IllegalArgumentException("Booking not found: " + id);
        }
        bookingRepo.deleteById(id);
    }


    /** Helper: 404 or entity **/
    public Booking getByIdOrThrow(Long id) {
        return bookingRepo.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }

    /** Mapping **/
    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getActivity().getId(),
                booking.getCustomer().getId(),
                booking.getStartDatetime(),
                booking.getParticipants(),
                booking.getBookingStatus(),
                booking.getInstructorName()
        );
    }

    /** Validation **/
    private void validateCreate(BookingRequest req) {
        if (req.activityId() == null) throw new IllegalArgumentException("activityId is required");
        if (req.customerId() == null) throw new IllegalArgumentException("customerId is required");
        if (req.startDatetime() == null) throw new IllegalArgumentException("startDateTime is required");
        requireFutureOrNow(req.startDatetime(), "startDateTime");
        if (req.participants() == null || req.participants() <= 0) {
            throw new IllegalArgumentException("participants must be > 0");
        }
    }

    private void requireFutureOrNow(LocalDateTime dt, String field) {
        // Drop denne hvis historiske bookinger skal være lovlige
        if (dt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(field + " must be in the future or now");
        }
    }
}