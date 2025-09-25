const API_BASE = '/api';

let currentActivities = [];
let currentReservations = [];

// DOM ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing...');
    loadAllActivities();
    loadActivitiesForBooking();
    setMinDateTime();
});

// Tab functionality
function showTab(tabName) {
    console.log('Switching to tab:', tabName);

    // Hide all tabs
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Remove active class from all buttons
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(btn => btn.classList.remove('active'));

    // Show selected tab
    document.getElementById(tabName + '-tab').classList.add('active');
    event.target.classList.add('active');

    // Load data when switching to specific tabs
    if (tabName === 'reservations') {
        loadReservations('all');
    } else if (tabName === 'booking') {
        // Reload activities for booking when switching to booking tab
        loadActivitiesForBooking();
    }
}

// Activities functionality
async function loadAllActivities() {
    console.log('Loading all activities...');
    try {
        const response = await fetch(`${API_BASE}/activities`);
        console.log('Activities response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        currentActivities = await response.json();
        console.log('Loaded activities:', currentActivities);
        displayActivities(currentActivities);
    } catch (error) {
        console.error('Fejl ved indlæsning af aktiviteter:', error);
        document.getElementById('activities-list').innerHTML = `
            <div style="padding: 20px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; color: #721c24;">
                Fejl ved indlæsning af aktiviteter: ${error.message}<br>
                <button class="btn" onclick="initTestData()" style="margin-top: 10px;">Prøv at indlæse test data først</button>
            </div>
        `;
    }
}

async function filterActivities() {
    const age = document.getElementById('age-filter').value;
    const participants = document.getElementById('participants-filter').value;

    console.log('Filtering with age:', age, 'participants:', participants);

    if (!age && !participants) {
        displayActivities(currentActivities);
        return;
    }

    try {
        let url = `${API_BASE}/activities`;
        if (age && participants) {
            url += `/suitable?age=${age}&participants=${participants}`;
        } else if (age) {
            url += `/for-age/${age}`;
        } else {
            // Filter locally for participants only
            const filtered = currentActivities.filter(activity =>
                activity.maxParticipants >= parseInt(participants)
            );
            displayActivities(filtered);
            return;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Kunne ikke filtrere aktiviteter');

        const filtered = await response.json();
        displayActivities(filtered);
    } catch (error) {
        console.error('Fejl ved filtrering:', error);
    }
}

function displayActivities(activities) {
    const container = document.getElementById('activities-list');

    if (!activities || activities.length === 0) {
        container.innerHTML = `
            <div style="padding: 20px; text-align: center; color: #666;">
                Ingen aktiviteter fundet
                <br><br>
                <button class="btn btn-success" onclick="initTestData()">Indlæs Test Data</button>
            </div>
        `;
        return;
    }

    const activitiesHtml = activities.map(activity => `
        <div class="activity-card">
            <div class="activity-title">${activity.name}</div>
            <p>${activity.description}</p>
            <div class="activity-info">
                <p><strong>Min. alder:</strong> ${activity.minAge} år</p>
                <p><strong>Max deltagere:</strong> ${activity.maxParticipants}</p>
                <p><strong>Varighed:</strong> ${activity.durationMinutes} min</p>
                <p><strong>Pris:</strong> ${activity.price} DKK</p>
                <p>
                    <strong>Udstyr:</strong> 
                    <span class="equipment-badge ${activity.equipmentRequired ? '' : 'not-required'}">
                        ${activity.equipmentRequired ? 'Krævet' : 'Ikke nødvendigt'}
                    </span>
                </p>
            </div>
        </div>
    `).join('');

    container.innerHTML = activitiesHtml;
}

// Booking functionality
async function loadActivitiesForBooking() {
    console.log('Loading activities for booking dropdown...');
    try {
        const response = await fetch(`${API_BASE}/activities`);
        console.log('Booking activities response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const activities = await response.json();
        console.log('Loaded activities for booking:', activities);

        const select = document.getElementById('activity-select');
        if (!select) {
            console.error('Activity select element not found!');
            return;
        }

        // Clear existing options
        select.innerHTML = '<option value="">Vælg aktivitet...</option>';

        // Add activities to dropdown
        activities.forEach(activity => {
            const option = document.createElement('option');
            option.value = activity.id;
            option.textContent = `${activity.name} - ${activity.price} DKK`;
            select.appendChild(option);
        });

        console.log('Activity dropdown populated with', activities.length, 'activities');

    } catch (error) {
        console.error('Fejl ved indlæsning af aktiviteter til booking:', error);

        // Show error in dropdown
        const select = document.getElementById('activity-select');
        if (select) {
            select.innerHTML = '<option value="">Fejl ved indlæsning - prøv at indlæs test data først</option>';
        }
    }
}

// Reservations functionality
async function loadReservations(type) {
    console.log('Loading reservations of type:', type);
    try {
        let url = `${API_BASE}/reservations`;

        switch(type) {
            case 'upcoming':
                url += '/upcoming';
                break;
            case 'private':
                url += '/type/PRIVATE';
                break;
            case 'corporate':
                url += '/type/CORPORATE';
                break;
            // 'all' uses base URL
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Kunne ikke hente reservationer');

        currentReservations = await response.json();
        console.log('Loaded reservations:', currentReservations);
        displayReservations(currentReservations);
    } catch (error) {
        console.error('Fejl ved indlæsning af reservationer:', error);
        document.getElementById('reservations-list').innerHTML = `
            <div style="padding: 20px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; color: #721c24;">
                Fejl ved indlæsning af reservationer: ${error.message}
            </div>
        `;
    }
}

function displayReservations(reservations) {
    const container = document.getElementById('reservations-list');

    if (!reservations || reservations.length === 0) {
        container.innerHTML = `
            <div style="padding: 20px; text-align: center; color: #666;">
                Ingen reservationer fundet
            </div>
        `;
        return;
    }

    const reservationsHtml = reservations.map(reservation => `
        <div class="reservation-card">
            <div class="reservation-title">${reservation.customerName}</div>
            <div class="activity-info">
                <p><strong>Aktivitet:</strong> ${reservation.activity?.name || 'Ukendt'}</p>
                <p><strong>Tid:</strong> ${formatDateTime(reservation.reservationTime)}</p>
                <p><strong>Deltagere:</strong> ${reservation.participantCount}</p>
                <p><strong>Type:</strong> ${reservation.type === 'PRIVATE' ? 'Privat' : 'Firmaaftale'}</p>
                <p><strong>Telefon:</strong> ${reservation.customerPhone || 'Ikke angivet'}</p>
                ${reservation.customerEmail ? `<p><strong>Email:</strong> ${reservation.customerEmail}</p>` : ''}
                ${reservation.notes ? `<p><strong>Noter:</strong> ${reservation.notes}</p>` : ''}
            </div>
        </div>
    `).join('');

    container.innerHTML = reservationsHtml;
}

// Handle booking form submission
document.addEventListener('DOMContentLoaded', function() {
    const bookingForm = document.getElementById('booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            console.log('Booking form submitted');

            const formData = {
                customerName: document.getElementById('customer-name').value,
                customerPhone: document.getElementById('customer-phone').value,
                customerEmail: document.getElementById('customer-email').value,
                participantCount: parseInt(document.getElementById('participant-count').value),
                reservationTime: document.getElementById('reservation-datetime').value,
                type: document.getElementById('reservation-type').value,
                notes: document.getElementById('notes').value,
                activity: { id: parseInt(document.getElementById('activity-select').value) }
            };

            console.log('Booking data:', formData);

            // Validate activity selection
            if (!formData.activity.id) {
                alert('Vælg venligst en aktivitet');
                return;
            }

            try {
                const response = await fetch(`${API_BASE}/reservations`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                console.log('Booking response status:', response.status);

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Server error: ${response.status} - ${errorText}`);
                }

                const reservation = await response.json();
                console.log('Booking created:', reservation);

                alert('Reservation oprettet successfully!');

                // Reset form
                bookingForm.reset();
                setMinDateTime(); // Reset min datetime

            } catch (error) {
                console.error('Fejl ved oprettelse af reservation:', error);
                alert(`Fejl ved oprettelse af reservation: ${error.message}`);
            }
        });
    }
});

// Initialize test data
async function initTestData() {
    console.log('Initializing test data...');
    try {
        const response = await fetch(`${API_BASE}/activities/init-data`, {
            method: 'POST'
        });

        console.log('Init data response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.text();
        console.log('Init data result:', result);

        alert('Test data indlæst!');

        // Reload data
        await loadAllActivities();
        await loadActivitiesForBooking();

    } catch (error) {
        console.error('Fejl ved initialisering af test data:', error);
        alert(`Fejl ved indlæsning af test data: ${error.message}`);
    }
}

// Utility functions
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('da-DK');
}

function setMinDateTime() {
    const datetimeInput = document.getElementById('reservation-datetime');
    if (datetimeInput) {
        const now = new Date();
        const tomorrow = new Date(now);
        tomorrow.setDate(tomorrow.getDate() + 1);

        const minDateTime = tomorrow.toISOString().slice(0, 16);
        datetimeInput.setAttribute('min', minDateTime);
        console.log('Set min datetime to:', minDateTime);
    }
}
