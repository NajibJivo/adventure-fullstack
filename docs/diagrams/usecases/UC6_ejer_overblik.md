# UC6: Se overblik/rapporter (read-only)
**Aktør(er):** Ejer  
**Mål:** Se nøglevisninger (fx dagens/ugens bookinger)  
**Præ-konditioner:** Bruger er OWNER  
**Post-konditioner:** Ingen ændring af data (read-only)

## Main Success Scenario
1) Ejer åbner “Overblik”.
2) Systemet viser nøgletal/visninger (fx bookings i dag/uge).
3) Ejer filtrerer dato/aktivitet.
4) Systemet opdaterer visningen.
5) Ingen ændringer kan foretages.

## Alternative/Failure Flows
- F1 – Uautoriseret: Ikke OWNER-rolle → afvis. (403)
- A1 – Ingen data i interval: Vis “Ingen data for perioden”. (200)
- F2 – For stort interval: Afvis/bed om snævrere filter. (400)

## Acceptance Criteria
- AC1: Kun role=OWNER kan tilgå ruten; ellers 403.
- AC2: Alle kald i overblik er read-only (ingen POST/PUT/DELETE).
