/** base URL **/ 
const api = {
  base: "/api/bookings",

  /** GET henter alle bookinger fra serveren
      await fetch(api.base) sender et GET request.
      Hvis serveren fx svarer med 200 OK, så returneres listen i JSON **/
  getAll: async () => {
    const res = await fetch(api.base);
    if (!res.ok) throw new Error(`Fejl ved hentning: ${res.status}`);
    return res.json();
  },

  /* POST fortæller serveren, at vi vil oprette noget nyt
     sender data fra formularen som JSON til backend */
  createBooking: async (booking) => {
    const res = await fetch(api.base, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(booking),
    });
    if (!res.ok) throw new Error(`Fejl ved oprettelse: ${res.status}`);
    return res.json();
  },

  /* PUT bruges til at opdatere  */
  updateBooking: async (id, booking) => {
    const res = await fetch(`${api.base}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(booking),
    });
    if (!res.ok) throw new Error(`Fejl ved opdatering: ${res.status}`);
    return res.json();
  },

  /* DELETE bruges til at slette */
  deleteBooking: async (id) => {
    const res = await fetch(`${api.base}/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error(`Fejl ved sletning: ${res.status}`);
  },

  getBookingById: async (id) => {
    const res = await fetch(`${api.base}/${id}`);
    if (!res.ok) throw new Error(`Fejl ved hentning: ${res.status}`);
    return res.json();
  },
};


// --- Hjælpere ---

/* Gør en ISO-dato, exempel ("2025-10-09T10:30:00") mere læsevenlig på dansk
   exempel resultat         "09. okt. 2025, 10:30" */
function formatDateTime(s) {
  if (!s) return "Ikke angivet";
  const d = new Date(s);
  return d.toLocaleString("da-DK", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/* Tilføjer sekunder (:00) til input fra <input type="datetime-local">, så backend altid får fuld ISO-tid */
function normalizeDateInput(value) {
  if (!value) return null;
  if (value.includes(":") && value.split(":").length === 2) return value + ":00";
  return value;
}


// --- Rendering ---
/* laver et HTML-kort for hver booking */
function createBookingCard(b) {
  return `
    <div class="activity-card">
      <div class="activity-content">
        <h3>${b.activityName || "Ukendt aktivitet"}</h3>
        <p><strong>Kunde:</strong> ${b.customerName || "Ukendt"}</p>
        <p><strong>Start:</strong> ${formatDateTime(b.startDatetime)}</p>
        <p><strong>Deltagere:</strong> ${b.participants}</p>
        <p><strong>Status:</strong> ${b.bookingStatus}</p>
        ${b.instructorName ? `<p><strong>Instruktør:</strong> ${b.instructorName}</p>` : ""}
        <button class="btn-edit" data-id="${b.id}">Rediger</button>
        <button class="btn-delete" data-id="${b.id}">Slet</button>
      </div>
    </div>
  `;
}

/* tager hele listen fra getAll() og viser alle kortene */
function displayBookings(list) {
  const container = document.getElementById("bookings-container");
  if (!list.length) {
    container.innerHTML = '<div class="loading">Ingen bookinger fundet.</div>';
    return;
  }
  container.innerHTML = list.map(createBookingCard).join("");
}

// --- Formulardata ---
/* læser inputfelterne i formularen */
function readBookingForm() {
  return {
    id: document.getElementById("id").value || null,
    activityId: Number(document.getElementById("activityId").value),
    customerId: Number(document.getElementById("customerId").value),
    startDatetime: document.getElementById("startDatetime").value,
    participants: Number(document.getElementById("participants").value),
    bookingStatus: document.getElementById("bookingStatus").value,
    instructorName: document.getElementById("instructorName").value.trim() || null,
  };
}

// --- Submit handler (CREATE) ---
/*  læser alle felter, opretter booking, 
    ger alert på booking er oprettet,
    genindlæser siden så at man ser den nye booking */
async function onBookingSubmit(e) {
  e.preventDefault();
  
  const data = readBookingForm();

  if (!data.activityId || !data.customerId) return alert("Vælg aktivitet og kunde");
  if (!data.startDatetime) return alert("Vælg startdato/tid");
  if (!data.participants || data.participants < 1) return alert("Antal deltagere skal være mindst 1");

  try {
    await api.createBooking({
      activityId: data.activityId,
      customerId: data.customerId,
      startDatetime: normalizeDateInput(data.startDatetime),
      participants: data.participants,
      bookingStatus: data.bookingStatus,
      instructorName: data.instructorName,
    });
    alert("Booking oprettet!");
    window.location.reload();
  } catch (err) {
    console.error(err);
    alert("Fejl: " + err.message);
  }
}

// --- Init ---
/* henter bookinger fra backend 
   viser dem i HTML 
   tilføjer event listeners til Gem, Rediger, Slet knapper */
document.addEventListener("DOMContentLoaded", async () => {
  let bookings = [];
  try {
    bookings = await api.getAll();
  } catch (err) {
    console.error(err);
  }

  displayBookings(bookings);

  const form = document.getElementById("booking-form");
  if (form) form.addEventListener("submit", onBookingSubmit);

  const container = document.getElementById("bookings-container");
  container.addEventListener("click", (e) => {
    const editBtn = e.target.closest(".btn-edit");
    if (editBtn) {
      window.location.href = `/edit-booking.html?id=${editBtn.dataset.id}`;
      return;
    }
    const delBtn = e.target.closest(".btn-delete");
    if (delBtn && confirm("Er du sikker på at du vil slette denne booking?")) {
      api.deleteBooking(delBtn.dataset.id).then(() => window.location.reload());
    }
  });

  // --- cancel button ---
  const cancelBtn = document.getElementById("btn-cancel");
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      form.reset();                  // ryd inputfelter
      document.getElementById("id").value = ""; // slet skjult id
      cancelBtn.hidden = true;       // skjul knappen igen
    });
  }

});
