// --- Helper: hent query-param fra URL ---
function getQueryParam(key) {
  const params = new URLSearchParams(window.location.search);
  return params.get(key);
}

// --- Cancel button: nulstil form ---
const cancelBtn = document.getElementById("btn-cancel");
cancelBtn.addEventListener("click", () => {
  const form = document.getElementById("booking-form");
  form.reset();                  // ryd inputfelter
  document.getElementById("id").value = ""; // slet skjult id
  cancelBtn.hidden = true;       // skjul knappen igen
});

// --- Load booking fra backend ---
async function loadBooking(id) {
  try {
    const booking = await api.getBookingById(id);

    document.getElementById("id").value = booking.id;
    document.getElementById("activityId").value = booking.activityId;
    document.getElementById("customerId").value = booking.customerId;
    document.getElementById("startDatetime").value = booking.startDatetime?.substring(0,16) || '';
    document.getElementById("participants").value = booking.participants;
    document.getElementById("bookingStatus").value = booking.bookingStatus;
    document.getElementById("instructorName").value = booking.instructorName || "";

  } catch (err) {
    console.error(err);
    alert("Kunne ikke hente booking");
  }
}

// --- Læs data fra form ---
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

// --- Submit handler: opdater booking ---
async function onEditSubmit(e) {
  e.preventDefault();
  const data = readBookingForm();
  const id = Number(data.id);
  if (!id) return alert("Ingen booking ID fundet");

  const payload = {
    activityId: data.activityId,
    customerId: data.customerId,
    startDatetime: normalizeDateInput(data.startDatetime),
    participants: data.participants,
    bookingStatus: data.bookingStatus,
    instructorName: data.instructorName || null,
  };

  try {
    await api.updateBooking(id, payload);
    alert("Booking opdateret!");
    window.location.href = "/booking.html"; // tilbage til oversigten
  } catch (err) {
    console.error(err);
    alert("Fejl ved opdatering: " + (err.message || err));
  }
}

// --- Init: når DOM er loaded ---
document.addEventListener("DOMContentLoaded", async () => {
  const id = getQueryParam("id");
  if (!id) return alert("Ingen booking ID");

  // Hent booking og fyld form
  await loadBooking(id);

  // Tilføj submit handler
  const form = document.getElementById("booking-form");
  if (form) form.addEventListener("submit", onEditSubmit);

  // Vis cancel knap
  cancelBtn.hidden = false;
});
