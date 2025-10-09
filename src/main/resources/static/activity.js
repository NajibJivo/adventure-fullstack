// --- Mock API (plastikmad) ---
// Hvorfor: Vi vil kunne færdiggøre US5.2 (UI-flow) uafhængigt af backend-konflikter.
// Antagelse: API returnerer et objekt inkl. id som backend normalt ville gøre.

const api = {
  /* base path, gør det nemmere at ændre endpoint ét sted */
  base: "/api/activities",

  /* hente alle aktiviteter fra serveren, backend
  fetch: sender en HTTP GET-anmodning
  res.ok tjekker, om svaret var succesfuldt.
  res.json() konverterer svaret til JavaScript-objekt fra JSON string */
  getAll: async () => {
    const res = await fetch("/api/activities");
    if (!res.ok) throw new Error(`Hentning fejlede: ${res.status}`);
    return res.json();
  },

  /* method: "POST" = vi sender ny data.
     headers fortæller, at vi sender JSON.
     body er selve aktiviteten i tekstformat.*/
  createActivity: async (activity) => {
    const res = await fetch("/api/activities", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(activity),
    });
    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || `Create fejlede: ${res.status}`);
    }
    return res.json();
  },

  updateActivity: async (id, activity) => {
    const res = await fetch(`/api/activities/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(activity),
    });
    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || `Update fejlede: ${res.status}`);
    }
    return res.json();
  },

  getActivityById: async (id) => {
    const res = await fetch(`/api/activities/${id}`);
    if (!res.ok) throw new Error(`Kunne ikke hente aktivitet: ${res.status}`);
    return res.json();
  },

  deleteActivity: async (id) => {
    const res = await fetch(`/api/activities/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error(`Delete fejlede: ${res.status}`);
    return;
  },
};

// --- Hjælpere ---
// Hvorfor: Ensartet visning af datoer/priser; centraliseret formattering
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
function formatPrice(p) {
  return new Intl.NumberFormat("da-DK", {
    style: "currency",
    currency: "DKK",
    minimumFractionDigits: 2,
  }).format(p);
}

/* tilføjer sekunderne :00 før vi sender date input til serveren 
  sådan at de kom med i backend hvor de forventes at være med. exempel 2024-10-09T10:30 
  2 dele efter split, betyder der er kun timer og minutter, mangler sekunder */
function normalizeDateInput(value) {
  if (!value) return null;
  if (value.includes(":") && value.split(":").length === 2) {
    return value + ":00";
  }
  return value;
}

// --- Rendering af aktivitetkort ---
// Hvorfor: Separat render-funktion gør det let at re-render efter create/update.
function createActivityCard(a) {
  return `
    <div class="activity-card">
      <div class="activity-image">
        <img src="${a.imageUrl}" alt="${a.name}" class="activity-img">
      </div>
      <div class="activity-content">
        <h3 class="activity-title">${a.name}</h3>
        <p class="activity-description">${a.description}</p>

        <div class="activity-details">
          <div class="detail-item"><span class="detail-label">Pris:</span> <span class="detail-value">${formatPrice(
            a.price
          )}</span></div>
          <div class="detail-item"><span class="detail-label">Varighed:</span> <span class="detail-value">${
            a.duration
          } min</span></div>
          <div class="detail-item"><span class="detail-label">Min. alder:</span> <span class="detail-value">${
            a.minAge
          } år</span></div>
          <div class="detail-item"><span class="detail-label">Min. højde:</span> <span class="detail-value">${
            a.minHeight > 0 ? a.minHeight + " cm" : "Ingen krav"
          }</span></div>
          <div class="detail-item"><span class="detail-label">Tilgængelig fra:</span> <span class="detail-value">${formatDateTime(
            a.availableFrom
          )}</span></div>
          <div class="detail-item"><span class="detail-label">Tilgængelig til:</span> <span class="detail-value">${formatDateTime(
            a.availableTo
          )}</span></div>
        </div>

        <button class="btn-edit" data-id="${a.id}">Rediger</button>
      </div>
    </div>
  `;
}

// Kører når DOM’en er helt loaded
document.addEventListener("DOMContentLoaded", async () => {
  // 1. Hent aktiviteter fra backend
  try {
    activities = await api.getAll(); // GET /api/activities
  } catch (err) {
    console.error("Kunne ikke hente aktiviteter fra server:", err);
    activities = []; // fallback: tom liste
  }

  // 2. Render aktivitetkort på siden
  displayActivities();

  // 3. Bind create-form submit
  const form = document.getElementById("activity-form");
  if (form) form.addEventListener("submit", onMiniSubmit);
  // onMiniSubmit håndterer kun CREATE (ny aktivitet)

  // 4. Bind edit-knapper til redirect
  const container = document.getElementById("activities-container");
  container.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-edit"); // find klik på en "Rediger"-knap
    if (!btn) return;
    // redirect til edit-side med query-param id
    window.location.href = `/edit-activity.html?id=${btn.dataset.id}`;
  });

  // --- 5. Bind cancel-knap til at nulstille formularen ---
  const cancelBtn = document.getElementById("btn-cancel");
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      form.reset(); // ryd inputfelter
      document.getElementById("id").value = ""; // slet skjult id
      const submitBtn = document.querySelector(
        '#activity-form button[type="submit"]'
      );
      if (submitBtn) submitBtn.textContent = "Gem"; // sæt tekst tilbage
      cancelBtn.hidden = true; // skjul knappen igen
    });
  }
});

// --- Formulardata
// reads all the values from the form and returns them as a single JavaScript object.---

function readMiniForm() {
  return {
    id: document.getElementById("id")?.value || null,
    name: document.getElementById("name")?.value.trim(),
    description: document.getElementById("description")?.value.trim(),
    price: Number(document.getElementById("price")?.value),
    duration: Number(document.getElementById("duration")?.value) || 0,
    minAge: Number(document.getElementById("minAge")?.value) || 0,
    minHeight: Number(document.getElementById("minHeight")?.value) || 0,
    availableFrom: document.getElementById("availableFrom")?.value || null,
    availableTo: document.getElementById("availableTo")?.value || null,
    imageUrl: document.getElementById("imageUrl")?.value.trim(),
  };
}

// --- Submit-handler (Create +) ---
async function onMiniSubmit(e) {
  e.preventDefault();
  const data = readMiniForm();

  if (!data.name || Number.isNaN(data.price) || data.price < 0) {
    return alert("Ugyldige input");
  }

  try {
    await api.createActivity({
      name: data.name,
      description: data.description,
      price: Number(data.price),
      duration: Number(data.duration) || 1,
      minAge: Number(data.minAge) || 0,
      minHeight: Number(data.minHeight) || 0,
      availableFrom: normalizeDateInput(data.availableFrom),
      availableTo: normalizeDateInput(data.availableTo),
      imageUrl: data.imageUrl || "",
    });

    alert("Aktivitet oprettet!");
    window.location.reload(); // refresh listen fra backend
  } catch (err) {
    console.error(err);
    alert("Fejl ved oprettelse: " + (err.message || err));
  }
}
