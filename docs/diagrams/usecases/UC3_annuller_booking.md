# UC3: Annullér booking
**Aktør(er):** Kunde  
**Mål:** Annullere en egen aktiv booking  
**Præ-konditioner:** Kunden er identificeret; booking findes og er CREATED  
**Post-konditioner:** Booking sat til CANCELLED; kapacitet frigivet

## Main Success Scenario
1) Kunden åbner “Mine bookinger”.
2) Kunden vælger en aktiv booking.
3) Kunden klikker “Annullér”.
4) Systemet bekræfter og ændrer status til CANCELLED.
5) Systemet viser opdateret liste.

## Alternative/Failure Flows
- A1 – Fortryd: Kunden afbryder annullering → ingen ændring.
- F1 – Ikke ejer af booking: Afvis. (403/404)
- F2 – Booking findes ikke/allerede annulleret: Vis besked. (404/409)

## Acceptance Criteria
- AC1: DELETE /bookings/{id} returnerer 204 (eller 200) og booking er ikke længere aktiv.
- AC2: Efter annullering kan timeslottet bookes igen (kapacitet frigivet).
