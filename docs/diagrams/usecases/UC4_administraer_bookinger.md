# UC4: Administrér bookinger
**Aktør(er):** Ansat  
**Mål:** Oprette/ændre/annullere bookinger og se dagsoversigt  
**Præ-konditioner:** Bruger er autentificeret med rolle EMPLOYEE  
**Post-konditioner:** Ændringer gemt i systemet

## Main Success Scenario
1) Ansat åbner “Booking-oversigt (dag)”.
2) Systemet viser bookinger pr. aktivitet/timeslot.
3) Ansat søger/filtrerer (dato, navn, aktivitet).
4) Ansat opretter/ændrer/annullerer på vegne af kunde.
5) Systemet gemmer og opdaterer oversigten.

## Alternative/Failure Flows
- A1 – Ingen bookinger for valgt dato: Vis “Ingen bookinger”. (200)
- F1 – Kapacitet overskredet ved opret/ændr: Afvis. (409)
- F2 – Uautoriseret: Mangler EMPLOYEE-rolle → afvis. (403)
- F3 – Ændring af historisk booking: Afvis iht. policy. (400/409)

## Acceptance Criteria
- AC1: GET /bookings?date=YYYY-MM-DD returnerer 200 og kan grupperes pr. aktivitet/tid.
- AC2: POST/PUT/DELETE kræver role=EMPLOYEE; ellers 403.
