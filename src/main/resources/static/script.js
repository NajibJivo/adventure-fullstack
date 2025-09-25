const API_BASE='/api';

let currenActivities=[];
let currentReservations = [];

// DOM ready
document.addEventListener('DOMContentLoaded', function() {
    loadAllActivities();
    loadActivitiesForBooking();
    setMinDateTime();
});

// Tab functionality
function showTab(tabName) {
    // Hide all tabs
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Remove active class from all buttons
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(btn => btn.classList.remove('active'));

    // Show selected tab
    document.getElementById(tabName + '-tab').classList.add('active');
    event.target.classList.add('active');

    // Load data when switching to reservations tab
    if (tabName === 'reservations') {
        loadReservations('all');
    }
}

// Activities functionality
async function loadAllActivities() {
    try {
        const response = await fetch(`${API_BASE}/activities`);
        if (!response.ok) throw new Error('Kunne ikke hente aktiviteter');

        currentActivities = await response.json();
        displayActivities(currentActivities);
    } catch (error) {
        console.error('Fejl ved indlæsning af aktiviteter:', error);
        document.getElementById('activities-list').innerHTML = 'Fejl ved indlæsning af aktiviteter';
    }
}

async function filterActivities() {
    const age = document.getElementById('age-filter').value;
    const participants = document.getElementById('participants-filter').value;

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

    if (activities.length === 0) {
        container.innerHTML = 'Ingen aktiviteter fundet';
        return;
    }

    const activitiesHtml = activities.map(activity => `
        
            ${activity.name}
            ${activity.description}
            
                Min. alder: ${activity.minAge} år
                Max deltagere: ${activity.maxParticipants}
                Varighed: ${activity.durationMinutes} min
                Pris: ${activity.price} DKK
                
                    Udstyr: 
                    ${activity.equipmentRequired ? 'Krævet' : 'Ikke nødvendigt'}
                
            
        
    `).join('');

    container.innerHTML = activitiesHtml;
}

// Reservations functionality
async function loadReservations(type) {
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
        displayReservations(currentReservations);
    } catch (error) {
        console.error('Fejl ved indlæsning af reservationer:', error);
        document.getElementById('reservations-list').innerHTML = 'Fejl ved indlæsning af reservationer';
    }
}

function displayReservations(reservations) {
    const container = document.getElementById('reservations-list');

    if (reservations.length === 0) {
        container.innerHTML = 'Ingen reservationer fundet';
        return;
    }

    const reservationsHtml = reservations.map(reservation => `
        
            ${reservation.customerName}
            
                Aktivitet: ${reservation.activity?.name || 'Ukendt'}
                Tid: ${formatDateTime(reservation.reservationTime)}
                Deltagere: ${reservation.participantCount}
                Type: ${reservation.type === 'PRIVATE' ? 'Privat' : 'Firmaaftale'}
                Telefon: ${reservation.customerPhone}
                ${reservation.customerEmail ? `Email: ${reservation.customerEmail}` : ''}
                ${reservation.notes ? `Noter: ${reservation.notes}` : ''}
            
        
    `).join('');

    container.innerHTML = reservationsHtml;
}

// Booking functionality
async function loadActivitiesForBooking() {
    try {
        const response = await fetch(`${API_BASE}/activities`);
        if (!response.ok) throw new Error('Kunne ikke hente aktiviteter');

        const activities = await response.json();
        const select = document.getElementById('activity-select');

        select.innerHTML = 'Vælg aktivitet...';
        activities.forEach(activity => {
            select.innerHTML += `${activity.name} - ${activity.price} DKK`;
        });
    } catch (error) {
        console.error('Fejl ved indlæsning af aktiviteter til booking:', error);
    }
}

// Handle booking form submission
document.getElementById('booking-form').addEventListener('submit', async function(e) {
    e.preventDefault();

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

    try {
        const response = await fetch(`${API_BASE}/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) throw new Error('Kunne ikke oprette reservation');

        const reservation = await response.json();
        alert('Reservation oprettet successfully!');

        // Reset form
        document.getElementById('booking-form').reset();

        // Switch to reservations tab if we want to show the new reservation
        // showTab('reservations');

    } catch (error) {
        console.error('Fejl ved oprettelse af reservation:', error);
        alert('Fejl ved oprettelse af reservation');
    }
});

// Initialize test data
async function initTestData() {
    try {
        const response = await fetch(`${API_BASE}/activities/init-data`, {
            method: 'POST'
        });

        if (!response.ok) throw new Error('Kunne ikke initialisere test data');

        alert('Test data indlæst!');
        loadAllActivities();
        loadActivitiesForBooking();

    } catch (error) {
        console.error('Fejl ved initialisering af test data:', error);
        alert('Fejl ved indlæsning af test data');
    }
}

// Utility functions
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('da-DK');
}

function setMinDateTime() {
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const minDateTime = tomorrow.toISOString().slice(0, 16);
    document.getElementById('reservation-datetime').setAttribute('min', minDateTime);
}