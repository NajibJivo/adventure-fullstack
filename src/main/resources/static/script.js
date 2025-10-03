// --- Mock API (plastikmad) ---
// Hvorfor: Vi vil kunne færdiggøre US5.2 (UI-flow) uafhængigt af backend-konflikter.
// Antagelse: API returnerer et objekt inkl. id som backend normalt ville gøre.
const api = {
  createActivity: async (activity) => {
    await new Promise(r => setTimeout(r, 200)); // simuler netværkslatens
    return { ...activity, id: Date.now() };
  },
  updateActivity: async (id, activity) => {
    await new Promise(r => setTimeout(r, 200)); // simuler netværkslatens
    return { ...activity, id: Number(id) };
  }
};

// --- Startdata (hardcoded) ---
// Hvorfor: UI skal vise noget fra start; i rigtig app ville dette komme fra GET /api/activities.
const activities = [
  {
    id: 1, name: "Go-kart",
    description: "Oplev spænding og fart på vores professionelle go-kart bane. Perfekt for adrenalinjunkies og dem der elsker konkurrence!",
    price: 150.00, duration: 15, minAge: 12, minHeight: 150,
    availableFrom: "2024-10-01T10:00:00", availableTo: "2024-12-31T20:00:00",
    imageUrl: "https://images.unsplash.com/photo-1652451991281-e637ec408bec?q=80&w=2233&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    colorClass: "gocart"
  },
  {
    id: 2, name: "Minigolf",
    description: "Hyggelig 18-hullers minigolf bane for hele familien. Tag udfordringen op og se hvem der kan score lavest!",
    price: 75.00, duration: 60, minAge: 5, minHeight: 0,
    availableFrom: "2024-10-01T10:00:00", availableTo: "2024-12-31T20:00:00",
    imageUrl: "https://images.unsplash.com/photo-1730198439547-413dc40624bf?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    colorClass: "minigolf"
  },
  {
    id: 3, name: "Paintball",
    description: "Taktisk teamwork og action! Kæmp mod dine venner i et spændende paintball-slag på vores udendørs bane.",
    price: 200.00, duration: 90, minAge: 16, minHeight: 0,
    availableFrom: "2024-10-01T10:00:00", availableTo: "2024-12-31T18:00:00",
    imageUrl: "https://paintballsports.co.uk/wp-content/uploads/2022/04/FF0924_2015_2_Bundesliga_Spieltag1-2-19-400x400.jpg",
    colorClass: "paintball"
  },
  {
    id: 4, name: "Sumo Wrestling",
    description: "Tag sumo-drakten på og udfordre dine venner i morsom sumo brydning. Garanteret sjovt for alle!",
    price: 100.00, duration: 20, minAge: 8, minHeight: 120,
    availableFrom: "2024-10-01T10:00:00", availableTo: "2024-12-31T20:00:00",
    imageUrl: "https://thumbs.dreamstime.com/b/sumo-wrestlers-4836066.jpg",
    colorClass: "sumo"
  }
];

// --- Hjælpere ---
// Hvorfor: Ensartet visning af datoer/priser; centraliseret formattering.
function formatDateTime(s) {
  if (!s) return "Ikke angivet";
  const d = new Date(s);
  return d.toLocaleString("da-DK", {
    day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit"
  });
}
function formatPrice(p) {
  return new Intl.NumberFormat("da-DK", {
    style: "currency", currency: "DKK", minimumFractionDigits: 2
  }).format(p);
}

// --- Rendering af aktivitetkort ---
// Hvorfor: Separat render-funktion gør det let at re-render efter create/update.
function createActivityCard(a) {
  return `
    <div class="activity-card">
      <div class="activity-image ${a.colorClass}">
        <img src="${a.imageUrl}" alt="${a.name}" class="activity-img">
      </div>
      <div class="activity-content">
        <h3 class="activity-title">${a.name}</h3>
        <p class="activity-description">${a.description}</p>

        <div class="activity-details">
          <div class="detail-item"><span class="detail-label">Varighed:</span> <span class="detail-value">${a.duration} min</span></div>
          <div class="detail-item"><span class="detail-label">Min. alder:</span> <span class="detail-value">${a.minAge} år</span></div>
          <div class="detail-item"><span class="detail-label">Min. højde:</span> <span class="detail-value">${a.minHeight > 0 ? a.minHeight + ' cm' : 'Ingen krav'}</span></div>
          <div class="detail-item"><span class="detail-label">Tilgængelig fra:</span> <span class="detail-value">${formatDateTime(a.availableFrom)}</span></div>
        </div>

        <div class="price-tag">${formatPrice(a.price)}</div>
        <button class="btn-edit" data-id="${a.id}">Rediger</button>
      </div>
    </div>
  `;
}
function displayActivities() {
  const container = document.getElementById("activities-container");
  if (!activities.length) {
    container.innerHTML = '<div class="loading">Ingen aktiviteter tilgængelige endnu.</div>';
    return;
  }
  container.innerHTML = activities.map(createActivityCard).join("");
}

// --- Formulardata ---
// Hvorfor: Én kilde til at læse felter; gør validering/submit enklere.
function readMiniForm() {
  const name = document.getElementById("name")?.value.trim();
  const price = Number(document.getElementById("price")?.value);
  return {
    id: document.getElementById("id")?.value || null, // tom => create, sat => update
    name, price
  };
}

// --- Edit flow ---
// Hvorfor: Indlæser valgt aktivitet i formularen for at aktivere update-flow.
function loadActivityToForm(id) {
  const a = activities.find(x => x.id === Number(id));
  if (!a) return;
  document.getElementById("id").value = a.id;     // switch til update-tilstand
  document.getElementById("name").value = a.name;
  document.getElementById("price").value = a.price;

  // UX: tydeliggør at næste submit bliver en opdatering
  const submitBtn = document.querySelector('#activity-form button[type="submit"]');
  if (submitBtn) submitBtn.textContent = "Opdater";

  document.getElementById('btn-cancel').hidden = false;   // 🔹 vis “Annuller”
}

// --- Submit-handler (Create/Update + feedback) ---
// Hvorfor: Ét sted der håndterer både create og update, så vi undgår duplikeret kode.
function onMiniSubmit(e) {
  e.preventDefault();

  // UI-validering: hurtig feedback til brugeren uden netværkskald.
  const data = readMiniForm();
  if (!data.name) { alert("Navn er påkrævet"); return; }
  if (Number.isNaN(data.price) || data.price < 0) { alert("Pris skal være ≥ 0"); return; }

  // UX-feedback: disable + “arbejder”-tekst mens mock simulerer netværk.
  const submitBtn = document.querySelector('#activity-form button[type="submit"]');
  const wasUpdate = !!data.id;
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = wasUpdate ? "Opdaterer…" : "Gemmer…";
  }

  // Mock-kald + lokal state-opdatering
  (async () => {
    if (!data.id) {
      // CREATE: vi tilføjer minimumsfelter; resten kan udfyldes senere.
      const created = await api.createActivity({ name: data.name, price: data.price });
      activities.push({
        ...created,
        description: created.description ?? "",
        duration: 0, minAge: 0, minHeight: 0,
        availableFrom: null, availableTo: null, imageUrl: "", colorClass: ""
      });
    } else {
      // UPDATE: find og erstat kun ændrede felter (name/price her).
      const updated = await api.updateActivity(data.id, { name: data.name, price: data.price });
      const ix = activities.findIndex(a => a.id === Number(updated.id));
      if (ix !== -1) activities[ix] = { ...activities[ix], ...updated };
    }

    // Re-render + reset form: bekræfter ændringen visuelt og går tilbage til create-tilstand.
    displayActivities();
    e.target.reset();
    document.getElementById("id").value = "";
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.textContent = "Gem";
    }
  })();
}

// --- Init (bind handlers) ---
// Hvorfor: Saml alle event-bindings ét sted for overblik og for at sikre de kun sættes én gang.
document.addEventListener("DOMContentLoaded", () => {
  displayActivities();

  const form = document.getElementById("activity-form");
  if (form) form.addEventListener("submit", onMiniSubmit);

  // Event delegation: én lytter til alle “Rediger”-knapper nu og i fremtiden.
  const container = document.getElementById("activities-container");
  container.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-edit");
    if (!btn) return;
    loadActivityToForm(btn.dataset.id);
  });

  // ---🔹 Annuller: nulstil og tilbage til “Gem” ---
  const cancelBtn = document.getElementById('btn-cancel');
  cancelBtn.addEventListener('click', () => {
    form.reset();
    document.getElementById('id').value = '';
    const submitBtn = document.querySelector('#activity-form button[type="submit"]');
    if (submitBtn) submitBtn.textContent = 'Gem';
    cancelBtn.hidden = true; // skjul igen
  });
});
