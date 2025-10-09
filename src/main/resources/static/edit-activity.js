// --- Helper: hent query-param: 
// Kort sagt: måde at få ID’et fra URL’en på, så vi ved hvad vi skal redigere---
function getQueryParam(key) {
  const params = new URLSearchParams(window.location.search);
  return params.get(key);
}

/* reset form hvis brugeren trykker cancel under create */
const cancelBtn = document.getElementById('btn-cancel');
cancelBtn.addEventListener('click', () => {
  const form = document.getElementById("activity-form");
  form.reset();
});

// --- Load aktivitet fra backend ---
async function loadActivity(id) {
  try {
    const activity = await api.getActivityById(id);
    document.getElementById("id").value = activity.id;
    document.getElementById("name").value = activity.name;
    document.getElementById("description").value = activity.description;
    document.getElementById("price").value = activity.price;
    document.getElementById("duration").value = activity.duration;
    document.getElementById("minAge").value = activity.minAge;
    document.getElementById("minHeight").value = activity.minHeight;
    document.getElementById("availableFrom").value = activity.availableFrom?.substring(0,16) || '';
    document.getElementById("availableTo").value = activity.availableTo?.substring(0,16) || '';
    document.getElementById("imageUrl").value = activity.imageUrl || "";
  } catch (err) {
    console.error(err);
    alert("Kunne ikke hente aktiviteten");
  }
}


// --- Submit handler for update 
// take all the values the user has typed in edit form and turn them into a JavaScript object ---
async function onEditSubmit(e) {
  e.preventDefault();
  const data = readMiniForm();
  const id = Number(data.id);
  if (!id) return alert("Ingen ID fundet");

  const payload = {
    name: data.name,
    description: data.description || "",
    price: Number(data.price) || 0,
    duration: Number(data.duration) > 0 ? Number(data.duration) : 1,
    minAge: Number.isFinite(Number(data.minAge)) ? Number(data.minAge) : 0,
    minHeight: Number.isFinite(Number(data.minHeight)) ? Number(data.minHeight) : 0,
    availableFrom: normalizeDateInput(data.availableFrom),
    availableTo: normalizeDateInput(data.availableTo),
    imageUrl: data.imageUrl || ""
  };

  try {
    await api.updateActivity(id, payload);
    alert("Aktivitet opdateret!");
    window.location.href = "/index.html"; // tilbage til oversigten
  } catch (err) {
    console.error(err);
    alert("Fejl ved opdatering: " + (err.message || err));
  }
}

// --- Init ---
document.addEventListener("DOMContentLoaded", () => {
  const id = getQueryParam("id");
  if (!id) return alert("Ingen aktivitet ID");

  loadActivity(id);
  document.getElementById("activity-form").addEventListener("submit", onEditSubmit);
});
