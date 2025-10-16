# UC2: Opret booking 

**Aktør(er):** Kunde (primær) / Ansat  
**Mål:** Oprette en booking på en aktivitet  
**Præ-konditioner:** Aktivitet findes; bruger kan se ledige tider  
**Post-konditioner:** Booking gemt og synlig i kalender

## Main Success Scenario
1) Aktør vælger aktivitet → klikker **Book**  
2) Vælger dato/tid og antal deltagere (evt. kontaktoplysninger)  
3) System validerer krav og kapacitet  
4) Aktør bekræfter  
5) System opretter booking og viser bekræftelse

## Alternative/Failure Flows (minimal)
- **F1 – Krav ikke opfyldt:** Alder/højde fejler → afvis med forklaring  
- **F2 – Ingen kapacitet:** Slot fuldt → foreslå andre tider  
- **F3 – Ugyldige felter:** Manglende/ugyldig dato/tid/antal → 400 Bad Request

## Acceptance Criteria (minimal)
- **AC1:** `POST /bookings` opretter booking ved success → **201 + Location**; ellers **400** ved valideringsfejl  
- **AC2:** Kapacitetsregel håndhæves; ved brud → **409 Conflict**  
- **AC3:** Alder-/højdekrav håndhæves; ved brud → **400/409** med klar årsag  
- **AC4:** Oprettet booking vises i kalender (dag/uge/måned) straks efter oprettelse
