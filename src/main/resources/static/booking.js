/** ROLLEBASERET BOOKING SYSTEM **/

// Brugerinfo
let currentUser = null;
let isAdmin = false;

// Hent nuværende bruger og rolle
async function getCurrentUser() {
    try {
        const res = await fetch('/api/auth/current');
        if (res.ok) {
            currentUser = await res.json();
            isAdmin = currentUser.role === 'OWNER' || currentUser.isAdmin;
            console.log('Bruger:', currentUser.name, '| Admin:', isAdmin);
        }
    } catch (err) {
        console.error('Kunne ikke hente brugerinfo:', err);
    }
}

// API
const api = {
    base: "/api/bookings",

    getAll: async () => {
        const res = await fetch(api.base);
        if (!res.ok) throw new Error(`Fejl ved hentning: ${res.status}`);
        return res.json();
    },

    createBooking: async (booking) => {
        const res = await fetch(api.base, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(booking),
        });
        if (!res.ok) throw new Error(`Fejl ved oprettelse: ${res.status}`);
        return res.json();
    },

    updateBooking: async (id, booking) => {
        const res = await fetch(`${api.base}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(booking),
        });
        if (!res.ok) throw new Error(`Fejl ved opdatering: ${res.status}`);
        return res.json();
    },

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

// Hjælpere
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

function normalizeDateInput(value) {
    if (!value) return null;
    if (value.includes(":") && value.split(":").length === 2) return value + ":00";
    return value;
}

// Load aktiviteter til dropdown
async function loadActivities() {
    try {
        const response = await fetch('/api/activities');
        const data = await response.json();
        const activities = data.content || data;

        const select = document.getElementById('activityId');
        select.innerHTML = '<option value="">-- Vælg aktivitet --</option>';
        activities.forEach(a => {
            select.innerHTML += `<option value="${a.id}">${a.name} - ${a.price} kr</option>`;
        });
    } catch (err) {
        console.error('Kunne ikke hente aktiviteter:', err);
    }
}

// Load kunder til dropdown (kun admin)
async function loadCustomers() {
    try {
        const response = await fetch('/api/customers');
        const customers = await response.json();

        const select = document.getElementById('customerId');
        select.innerHTML = '<option value="">-- Vælg kunde --</option>';
        customers.forEach(c => {
            select.innerHTML += `<option value="${c.id}">${c.name} (${c.email})</option>`;
        });
    } catch (err) {
        console.error('Kunne ikke hente kunder:', err);
    }
}

// Rendering
function createBookingCard(b) {
    const editDeleteButtons = isAdmin ? `
    <button class="btn-edit" data-id="${b.id}">Rediger</button>
    <button class="btn-delete" data-id="${b.id}">Slet</button>
  ` : '';

    return `
    <div class="activity-card">
      <div class="activity-content">
        <h3>Booking #${b.id}</h3>
        ${isAdmin ? `<p><strong>Kunde:</strong> ${b.customerName || "Ukendt"}</p>` : ''}
        <p><strong>Start:</strong> ${formatDateTime(b.startDateTime)}</p>
        <p><strong>Deltagere:</strong> ${b.participants}</p>
        <p><strong>Status:</strong> ${b.bookingStatus}</p>
        ${b.instructorName && isAdmin ? `<p><strong>Instruktør:</strong> ${b.instructorName}</p>` : ""}
        ${editDeleteButtons}
      </div>
    </div>
  `;
}

function displayBookings(list) {
    const container = document.getElementById("bookings-container");
    if (!list.length) {
        container.innerHTML = '<div class="loading">Ingen bookinger fundet.</div>';
        return;
    }
    container.innerHTML = list.map(createBookingCard).join("");
}

// Submit handler
async function onBookingSubmit(e) {
    e.preventDefault();

    const activityId = Number(document.getElementById("activityId").value);
    const startDatetime = document.getElementById("startDatetime").value;
    const participants = Number(document.getElementById("participants").value);

    // Valider
    if (!activityId) return alert("Vælg en aktivitet");
    if (isAdmin && !document.getElementById("customerId").value) return alert("Vælg en kunde");
    if (!startDatetime) return alert("Vælg startdato/tid");
    if (!participants || participants < 1) return alert("Antal deltagere skal være mindst 1");

    // Byg payload
    const payload = {
        activityId,
        customerId: isAdmin
            ? Number(document.getElementById("customerId").value)
            : currentUser.id,  // Kunde booker til sig selv
        startDateTime: normalizeDateInput(startDatetime),  // ÆNDRET: startDateTime
        participants,
        bookingStatus: isAdmin
            ? document.getElementById("bookingStatus").value
            : "PENDING",  // Kunder laver altid PENDING bookings
        instructorName: isAdmin
            ? document.getElementById("instructorName").value.trim() || null
            : null
    };

    try {
        await api.createBooking(payload);
        alert("Booking oprettet!");
        window.location.reload();
    } catch (err) {
        console.error(err);
        alert("Fejl: " + err.message);
    }
}

// Init
document.addEventListener("DOMContentLoaded", async () => {
    // 1. Hent brugerinfo FØRST
    await getCurrentUser();

    // 2. Vis/skjul felter baseret på rolle
    const customerField = document.getElementById('customer-field');
    const statusField = document.getElementById('status-field');
    const instructorField = document.getElementById('instructor-field');

    if (isAdmin) {
        customerField.style.display = 'flex';
        statusField.style.display = 'flex';
        instructorField.style.display = 'flex';
        await loadCustomers();
    } else {
        customerField.style.display = 'none';
        statusField.style.display = 'none';
        instructorField.style.display = 'none';
    }

    // 3. Hent aktiviteter
    await loadActivities();

    // 4. Hent bookinger
    let bookings = [];
    try {
        bookings = await api.getAll();

        // Filtrer: Kunder ser kun egne bookinger
        if (!isAdmin && currentUser) {
            bookings = bookings.filter(b => b.customerId === currentUser.id);
        }
    } catch (err) {
        console.error(err);
    }

    displayBookings(bookings);

    // 5. Form submit
    const form = document.getElementById("booking-form");
    if (form) form.addEventListener("submit", onBookingSubmit);

    // 6. Bind knapper
    const container = document.getElementById("bookings-container");
    container.addEventListener("click", (e) => {
        const editBtn = e.target.closest(".btn-edit");
        if (editBtn && isAdmin) {
            window.location.href = `/edit-booking.html?id=${editBtn.dataset.id}`;
            return;
        }
        const delBtn = e.target.closest(".btn-delete");
        if (delBtn && isAdmin && confirm("Er du sikker på at du vil slette denne booking?")) {
            api.deleteBooking(delBtn.dataset.id).then(() => window.location.reload());
        }
    });

    // 7. Cancel button
    const cancelBtn = document.getElementById("btn-cancel");
    if (cancelBtn) {
        cancelBtn.addEventListener("click", () => {
            form.reset();
            document.getElementById("id").value = "";
            cancelBtn.hidden = true;
        });
    }
});