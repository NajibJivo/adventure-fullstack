# Domænemodel – AdventureXP (MVP)

**Formål:** Analyse-diagram på forretningsniveau. Viser begreber og regler – ikke SQL-typer.

**Klasser:**
- **Activity:** id, name, description, price, duration, capacity
- **Booking:** id, startDateTime, participants, status {CREATED, CANCELLED}
- **User:** id, name, phone, email, role {CUSTOMER, EMPLOYEE, OWNER}

**Relationer:**
- Activity **1 — * Booking**
- User **1 — * Booking**

**Regler (invarianter):**
- `participants > 0`
- Kapacitet: **sum(participants)** pr. *(activity, startDateTime)* **≤** `Activity.capacity`
- Booking i fremtiden; annullering sætter `status = CANCELLED`



![Domain](./domainmodel_adventure_mvp.png)

