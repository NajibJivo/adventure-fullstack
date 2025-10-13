package com.example.miniProjekt.web.mapper;

import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.web.dto.BookingResponse;

import java.util.List;

public class BookingDtoMapper {
    private BookingDtoMapper() {}

    public static BookingResponse toResponse(Booking b) {
        Long activityId = (b.getActivity() != null) ? b.getActivity().getId() : null;
        Long customerId = (b.getCustomer() != null) ? b.getCustomer().getId() : null;

        return new BookingResponse(
                b.getId(),
                activityId,
                customerId,
                b.getStartDateTime(),
                b.getParticipants(),
                b.getBookingStatus(),        // <-- entity-felt 'status' mappes til DTO-felt 'bookingStatus'
                b.getInstructorName() // <-- kræver at Booking har getInstructorName()
        );
    }

    public static List<BookingResponse> toResponseList(List<Booking> list) {
        return list.stream().map(BookingDtoMapper::toResponse).toList();
    }
}
