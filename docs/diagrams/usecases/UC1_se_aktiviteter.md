# Use Case – AdventureXP (MVP)
**Aktører:** Kunde, Ansat, (Ejer – read-only)  
**Use cases (MVP):**
1) Se aktiviteter (Kunde)  
2) Opret booking (Kunde)  
3) Annullér booking (Kunde)  
4) Administrér bookinger (Ansat)  
5) Vedligehold aktiviteter (Ansat)  
*(Ejer: Se overblik/rapporter – læseadgang)*

**Formål:** Viser funktionelt scope på højt niveau.  
![Use Case](./usecase_adventureXP.png)


# UC1: Se aktiviteter (Minimal)

**Aktør(er):** Kunde (primær) / Ansat (sekundær)  
**Mål:** Finde en aktivitet og se detaljer/tilgængelighed  
**Præ-konditioner:** Der findes publicerede aktiviteter  
**Post-konditioner:** Liste/detalje vises; valgt aktivitet kan **bookes**

## Main Success Scenario
1) Aktør åbner **Se aktiviteter**  
2) System viser liste med søg, filter og sortering  
3) Aktør anvender søg/filter (fx dato/periode, pris, krav)  
4) Aktør vælger en aktivitet  
5) System viser detaljer (beskrivelse, pris, varighed, krav, udstyr, ledige tider) + **Book**-knap

## Alternative/Failure Flows (minimal)
- **F1 – Ingen match:** Vis “Ingen resultater” + mulighed for at nulstille filtre  
- **F2 – Netværk/serverfejl:** Vis fejlbesked og **Prøv igen**  
- **F3 – Ugyldige filterparametre:** 400 Bad Request; UI nulstilles til standardfiltre

## Acceptance Criteria (minimal)
- **AC1:** `GET /activities` → **200** med liste [id, name, price, duration, capacity, requirements(minAge/minHeight), availability/period]  
- **AC2:** Endpoint understøtter query params: `q`, `dateFrom/dateTo` (eller `period`), `sort`, `page`, `size`  
- **AC3:** `GET /activities/{id}` → **200** med detaljer inkl. udstyr og ledige tider; **404** hvis ikke fundet  
- **AC4:** 0 match returneres som tom liste (**200**); UI viser tom-tilstand
