// --- Mock API to temporarily simulate connection with backend ---
const api = {
  createBooking: async (booking) => {
    await new Promise(r => setTimeout(r, 200));
    return { ...booking, id: Date.now() };
  },
  updateBooking: async (id, booking) => {
    await new Promise(r => setTimeout(r, 200));
    return { ...booking, id: Number(id) };
  },
  deleteBooking: async (id) => {
    await new Promise(r => setTimeout(r, 150));
    return { success: true };
  }
};

//** Data arrays to temporarily hold fake data f*//
// --- Activities (genbrugt fra activity.js) ---
const activities = [
  {
    id: 1, name: "Go-kart", duration: 15,
    imageUrl: "https://images.unsplash.com/photo-1652451991281-e637ec408bec?q=80&w=2233&auto=format&fit=crop",
    colorClass: "gocart"
  },
  {
    id: 2, name: "Minigolf", duration: 60,
    imageUrl: "https://images.unsplash.com/photo-1730198439547-413dc40624bf?q=80&w=2070&auto=format&fit=crop",
    colorClass: "minigolf"
  },
  {
    id: 3, name: "Paintball", duration: 90,
    imageUrl: "https://paintballsports.co.uk/wp-content/uploads/2022/04/FF0924_2015_2_Bundesliga_Spieltag1-2-19-400x400.jpg",
    colorClass: "paintball"
  },
  {
    id: 4, name: "Sumo Wrestling", duration: 20,
    imageUrl: "https://thumbs.dreamstime.com/b/sumo-wrestlers-4836066.jpg",
    colorClass: "sumo"
  }
];

// --- Customers (tom for nu) ---
const customers = [];

// --- Startdata ---
const bookings = [
  {
    id: 1, activityId: 1, customerId: null,
    startDatetime: "2024-12-15T14:00", participants: 2,
    bookingStatus: "CONFIRMED", instructorName: "Lars Nielsen"
  },
  {
    id: 2, activityId: 2, customerId: null,
    startDatetime: "2024-12-20T10:30", participants: 4,
    bookingStatus: "PENDING", instructorName: null
  }
];

// --- Hjælpere ---
/** Formaterer dato til dansk format som er mere læsbar  **/ 
function formatDateTime(s) {
  if (!s) return "Ikke angivet";
  const d = new Date(s);
  return d.toLocaleString("da-DK", {
    day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit"
  });
}

function getActivityById(id) {
  return activities.find(a => a.id === Number(id));
}

function translateStatus(status) {
  const map = { 'PENDING': 'Afventende', 'CONFIRMED': 'Bekræftet', 'CANCELLED': 'Aflyst' };
  return map[status] || status;
}

// --- Rendering ---
function createBookingCard(b) {
  const activity = getActivityById(b.activityId);
  const activityName = activity ? activity.name : "Ukendt aktivitet";
  const activityImage = activity ? activity.imageUrl : "";
  const activityColor = activity ? activity.colorClass : "gocart";
  
  let statusClass = "status-pending";
  if (b.bookingStatus === "CONFIRMED") statusClass = "status-confirmed";
  if (b.bookingStatus === "CANCELLED") statusClass = "status-cancelled";

  return `
    <div class="activity-card">
      <div class="activity-image ${activityColor}">
        ${activityImage ? `<img src="${activityImage}" alt="${activityName}" class="activity-img">` : ''}
      </div>
      <div class="activity-content">
        <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 10px;">
          <h3 class="activity-title">${activityName}</h3>
          <span class="category-badge ${statusClass}">${translateStatus(b.bookingStatus)}</span>
        </div>

        <div class="activity-details">
          <div class="detail-item"><span class="detail-label">Kunde:</span> <span class="detail-value">${b.customerId || 'Ikke valgt'}</span></div>
          <div class="detail-item"><span class="detail-label">Start tid:</span> <span class="detail-value">${formatDateTime(b.startDatetime)}</span></div>
          <div class="detail-item"><span class="detail-label">Deltagere:</span> <span class="detail-value">${b.participants} personer</span></div>
          ${b.instructorName ? `<div class="detail-item"><span class="detail-label">Instruktør:</span> <span class="detail-value">${b.instructorName}</span></div>` : ''}
        </div>

        <button class="btn-edit" data-id="${b.id}">Rediger</button>
        <button class="btn-delete" data-id="${b.id}">Slet</button>
      </div>
    </div>
  `;
}

function displayBookings() {
  const container = document.getElementById("bookings-container");
  if (!bookings.length) {
    container.innerHTML = '<div class="loading">Ingen bookinger tilgængelige endnu.</div>';
    return;
  }
  container.innerHTML = bookings.map(createBookingCard).join("");
}

// --- Formulardata ---
function readBookingForm() {
  return {
    id: document.getElementById("booking-id")?.value || null,
    activityId: Number(document.getElementById("activity-id")?.value),
    customerId: document.getElementById("customer-id")?.value ? Number(document.getElementById("customer-id").value) : null,
    startDatetime: document.getElementById("start-datetime")?.value,
    participants: Number(document.getElementById("participants")?.value),
    bookingStatus: document.getElementById("booking-status")?.value,
    instructorName: document.getElementById("instructor-name")?.value.trim() || null
  };
}

// --- Edit flow ---
function loadBookingToForm(id) {
  const b = bookings.find(x => x.id === Number(id));
  if (!b) return;

  document.getElementById("booking-id").value = b.id;
  document.getElementById("activity-id").value = b.activityId;
  document.getElementById("customer-id").value = b.customerId || "";
  document.getElementById("start-datetime").value = b.startDatetime ? b.startDatetime.substring(0,16) : '';
  document.getElementById("participants").value = b.participants;
  document.getElementById("booking-status").value = b.bookingStatus;
  document.getElementById("instructor-name").value = b.instructorName || "";

  const submitBtn = document.querySelector('#booking-form button[type="submit"]');
  if (submitBtn) submitBtn.textContent = "Opdater";
  document.getElementById('btn-cancel').hidden = false;
}

// --- Submit-handler ---
function onBookingSubmit(e) {
  e.preventDefault();

  const data = readBookingForm();
  if (!data.activityId) { alert("Vælg en aktivitet"); return; }
  if (!data.startDatetime) { alert("Vælg start dato/tid"); return; }
  if (!data.participants || data.participants < 1) { alert("Antal deltagere skal være mindst 1"); return; }

  const submitBtn = document.querySelector('#booking-form button[type="submit"]');
  const wasUpdate = !!data.id;
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = wasUpdate ? "Opdaterer…" : "Gemmer…";
  }

  (async () => {
    if (!data.id) {
      const created = await api.createBooking(data);
      bookings.push(created);
    } else {
      const updated = await api.updateBooking(data.id, data);
      const ix = bookings.findIndex(b => b.id === Number(updated.id));
      if (ix !== -1) bookings[ix] = updated;
    }

    displayBookings();
    e.target.reset();
    document.getElementById("booking-id").value = "";
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.textContent = "Gem";
    }
    document.getElementById('btn-cancel').hidden = true;
  })();
}

// --- Delete handler ---
function onBookingDelete(id) {
  const booking = bookings.find(b => b.id === Number(id));
  if (!booking) return;
  
  const activity = getActivityById(booking.activityId);
  const name = activity ? activity.name : "denne booking";
  
  if (!confirm(`Slet ${name} booking?`)) return;

  (async () => {
    await api.deleteBooking(id);
    const ix = bookings.findIndex(b => b.id === Number(id));
    if (ix !== -1) bookings.splice(ix, 1);
    displayBookings();
  })();
}

// --- Populér dropdowns ---
function populateActivityDropdown() {
  const select = document.getElementById("activity-id");
  if (!select) return;
  const options = activities.map(a => `<option value="${a.id}">${a.name} (${a.duration} min)</option>`).join("");
  select.innerHTML = '<option value="">-- Vælg aktivitet --</option>' + options;
}

function populateCustomerDropdown() {
  const select = document.getElementById("customer-id");
  if (!select) return;
  if (customers.length === 0) {
    select.innerHTML = '<option value="">-- Ingen kunder tilgængelige --</option>';
  } else {
    const options = customers.map(c => `<option value="${c.id}">${c.name}</option>`).join("");
    select.innerHTML = '<option value="">-- Vælg kunde --</option>' + options;
  }
}

// --- Init ---
document.addEventListener("DOMContentLoaded", () => {
  displayBookings();
  populateActivityDropdown();
  populateCustomerDropdown();

  const form = document.getElementById("booking-form");
  if (form) form.addEventListener("submit", onBookingSubmit);

  const container = document.getElementById("bookings-container");
  container.addEventListener("click", (e) => {
    const editBtn = e.target.closest(".btn-edit");
    if (editBtn) {
      loadBookingToForm(editBtn.dataset.id);
      return;
    }
    
    const deleteBtn = e.target.closest(".btn-delete");
    if (deleteBtn) {
      onBookingDelete(deleteBtn.dataset.id);
      return;
    }
  });

  const cancelBtn = document.getElementById('btn-cancel');
  cancelBtn.addEventListener('click', () => {
    form.reset();
    document.getElementById('booking-id').value = '';
    const submitBtn = document.querySelector('#booking-form button[type="submit"]');
    if (submitBtn) submitBtn.textContent = 'Gem';
    cancelBtn.hidden = true;
  });
});