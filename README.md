# 🎯 AdventureXP 
AdventureXP er et semesterprojekt, hvor vi udvikler et IT-system til en aktivitets- og oplevelsespark, der skal håndtere reservationer, aktiviteter, udstyr og arrangementer for både private og virksomheder.

# 📌 Projektbeskrivelse

AdventureXP tilbyder oplevelser som paintball, klatring, gokart og andre events. I dag foregår booking primært manuelt, hvilket er ineffektivt. Med det nye system kan både kunder og medarbejdere administrere aktiviteter og arrangementer digitalt.
Forretningskontekst:
- Kunder kan reservere aktiviteter (enkeltvis eller som pakkeløsning til firma-events).
- Hver aktivitet har:
  - Aldersgrænse
  - Maks. antal deltagere
  - Pris
  - Varighed
 - Udstyr (fx hjelme, paintball-geværer) er knyttet til aktiviteter og skal kunne vedligeholdes.
 - Virksomhedskunder kan booke flere aktiviteter samlet som et arrangement.
 - Medarbejdere kan:
   - Oprette/ændre aktiviteter
   - Administrere reservationer
   - Se udstyrsstatus (vedligeholdelse)
  
# ⚙️ Teknologi & Projektstruktur
Vi arbejder agilt efter Scrum/XP-principper.

- Projektstyring: GitHub Projects (backlog, burndown charts, sprint boards)
- Versionkontrol: GitHub (separate repos til frontend og backend)
- Branching strategi:
  - Feature branch → dev → main
  - Alle merges sker via pull requests & code reviews
- Dokumentation & diagrammer:
  - Use case diagrammer (user stories)
  - ER-diagram (datamodel: aktiviteter, kunder, reservationer, udstyr)
  - UI wireframes (bookingflow, admin-dashboard)
  

## 🐳 Docker Setup

### Quick Start
```bash
# Start systemet
docker compose up -d --build

# Initialiser test data
curl -X POST http://localhost:8080/api/activities/init-data

# Stop systemet
docker compose down
```

### Adgang
- **Frontend & API:** http://localhost:8080
- **Database Admin:** http://localhost:8081
  - Server: `mysql`
  - Username: `appuser`
  - Password: `apppassword`
  - Database: `adventurepark`

### Nyttige Kommandoer
```bash
# Se logs
docker compose logs -f app

# Tjek status
docker compose ps

# Restart efter kodeændringer
docker compose restart app

# Fuld reset (sletter data!)
docker compose down -v
```

### Krav
- Docker Desktop installeret
- Port 8080 og 8081 skal være ledige

**Fuld Docker dokumentation findes i projektets rapport (User Story 2).**

# 📝 User Stories (eksempler)
- Som kunde vil jeg kunne reservere en aktivitet online, så jeg ikke skal ringe.
- Som virksomhed vil jeg kunne booke flere aktiviteter til en samlet event, så jeg kan planlægge firmaudflugt.
- Som medarbejder vil jeg kunne se og opdatere reservationer, så programmet altid er korrekt.
- Som tekniker vil jeg kunne markere udstyr som vedligeholdt, så sikkerheden er i orden.

# 👥 Team Roller
- Product Owner (PO): Ansvarlig for backlog & prioritering
- Scrum Master (SM): Faciliterer sprint-ritualer, fjerner blockers
- Developers (DEV): Implementering, reviews, tests
- DevOps: Opsætning af miljøer, CI, Docker
- UX/Design: Leverer design & wireframes

# 📂 Repo struktur (forslag)
AdventureXP/

│── backend/ # Spring Boot / Java

│── frontend/ # HTML, CSS, JS

│── docs/ # Diagrammer, backlog, rapport

│── .github/ # CI workflows

│── README.md

# ✅ Mål
At levere et færdigt system, der gør AdventureXP i stand til at:
- Håndtere reservationer og firmaarrangementer digitalt
- Administrere aktiviteter og udstyr fleksibelt
- Skabe bedre flow og overblik for både kunder og medarbejdere


# 📊 Diagrammer 
## <img width="273" height="74" alt="image" src="https://github.com/user-attachments/assets/85ca070b-a3c6-43f2-b1f4-0771dfcee952"/>

- **Context:** [PNG](docs/diagrams/context/context.png) · [Forklaring](docs/diagrams/context/context.md)
- **Use Case:** [PNG](docs/diagrams/usecase/usecase_adventureXP.png) · [Forklaring](docs/diagrams/usecase/usecase.md)
- **Domænemodel:** [PNG](docs/diagrams/domain/domainmodel_adventure_mvp.png) · [Forklaring](docs/diagrams/domain/domain.md)
- **ER-model:** [PNG](docs/diagrams/erd/erd_AdventureXP.png) · [Forklaring](docs/diagrams/erd/erd.md)


# 💼 Use Cases
- **UC1:** [Se aktiviteter](docs/diagrams/usecases/UC1_se_aktiviteter.md)
- **UC2:** [Opret booking](docs/diagrams/usecases/UC2_opret_booking.md)
- **UC3:** [Annullér booking](docs/diagrams/usecases/UC3_annuller_booking.md)
- **UC4:** [Administrér bookinger](docs/diagrams/usecases/UC4_administraer_bookinger.md)
- **UC5:** [Vedligehold aktiviteter](docs/diagrams/usecases/UC5_vedligehold_aktiviteter.md)
- **(Valgfri) UC6:** [Ejer – overblik/rapporter](docs/diagrams/usecases/UC6_ejer_overblik.md)

