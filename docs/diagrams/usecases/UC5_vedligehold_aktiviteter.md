# UC5: Vedligehold aktiviteter
**Aktør(er):** Ansat  
**Mål:** Oprette, redigere og slette aktiviteter  
**Prae-konditioner:** Bruger er EMPLOYEE; aktivitet kan have 0..n bookinger  
**Post-konditioner:** Aktivitet oprettet/ændret/slettet (med regler)

## Main Success Scenario
1) Ansat åbner “Aktiviteter (admin)”.
2) Systemet viser liste med redigér/slet/ny.
3) Ansat opretter/ændrer felter (navn, pris, varighed, capacity, beskrivelse).
4) Systemet validerer og gemmer.
5) Listen opdateres.

## Alternative/Failure Flows
- F1 – Ugyldige felter: Negativ pris, capacity ≤ 0, manglende navn → afvis. (400)
- F2 – Sletning med aktive bookinger: Afvis; vis antal berørte bookinger. (409)
- F3 – Capacity reduceres under eksisterende deltagere i et slot: Afvis/kræv plan. (409)
- A1 – Ingen aktiviteter: Vis tom tilstand “Opret ny”. (200)

## Acceptance Criteria
- AC1: POST/PUT /activities validerer obligatoriske felter; korrekte statuskoder (201/200/400).
- AC2: DELETE /activities/{id} afviser ved aktive bookinger (409), ellers 204.
