# ER-model – AdventureXP (visualisering)
# ERD – kort forklaring (case-minimum)

## Hvad tabellerne bruges til i praksis

**Customer**  
Kunden (privat/firma) der booker eller køber. Bruges i **Booking**, **Arrangement** og (i vores model) **Roster**.

**Activity**  
Selve aktiviteten (fx Gokart, Paintball) med varighed, alders-/højdekrav og periode/tilgængelighed. Bruges i “Se aktiviteter” og ved opret/redigér/slet.

**Equipment**  
Opslagsliste over udstyrstyper (hjelm, redningsvest, …). Vises som “equipment” tilknyttet en aktivitet.

**ActivityEquipment**  
Koblingstabel (M:N) der angiver hvilket udstyr en aktivitet kræver. Opdateres når en aktivitet ændres.

**Booking**  
En konkret booking: hvilken aktivitet, hvilken kunde, dato/tid, antal deltagere og instruktørnavn (tildelt “by name”). Danner grundlag for kalender (dag/uge/måned) og understøtter book/søg/ret/annullér.

**Roster**  
Vagtplan pr. instruktørnavn og dag (“assign an instructor by name to work on certain days”). I vores model er den knyttet til **Customer** (rolle = employee).

**Arrangement**  
Overligger for et firmaarrangement (kunde + titel + dato).

**ArrangementActivity**  
Koblingstabel (M:N), så et firmaarrangement kan omfatte flere aktiviteter (og samme aktivitet kan indgå i flere arrangementer).

**Product**  
Varer i kiosken (T-shirts, slik, sodavand) med pris, så vi kan oprette nye og redigere priser.

**Sale** & **SaleLine**  
Registrerer salg i kiosken: ét salg kan bestå af flere varer (antal og enhedspris). Prisændringer fremad sker på **Product**; historiske salg bevares via **SaleLine.unit_price**.

---

## Hvordan relationerne spiller sammen (hverdagsscenarier)

1) **Kunden browser aktiviteter**  
   Læs fra **Activity** (+ tilknyttet udstyr via **ActivityEquipment → Equipment**).

2) **Book en tid**  
   Opret **Booking** med *(activity_id, customer_id, startDateTime, participants, instructor_name)*. Kalender vises ud fra *startDateTime*.

3) **Vagtplan**  
   Opret en række i **Roster** for *(instruktørnavn, dato)*. Bruges som støtte, når instruktørnavn sættes på en booking.

4) **Firmaarrangement**  
   Opret **Arrangement** (kunde, dato, titel) og tilføj aktiviteter i **ArrangementActivity**.

5) **Salg i kiosken**  
   Opret **Sale** (dato/tid) og tilføj **SaleLine** for hver vare *(product_id, quantity, unit_price)*.


![ER](./erd_AdventureXP.png)
