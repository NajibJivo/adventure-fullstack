// HARDCODED BASE AKTIVITETER
const activities = [
    {
        id: 1,
        name: "Go-kart",
        description: "Oplev spænding og fart på vores professionelle go-kart bane. Perfekt for adrenalinjunkies og dem der elsker konkurrence!",
        price: 150.00,
        duration: 15,
        minAge: 12,
        minHeight: 150,
        availableFrom: "2024-10-01T10:00:00",
        availableTo: "2024-12-31T20:00:00",

        /* all image URL's are from unsplash.com or pixabay.com*/
        imageUrl: "https://images.unsplash.com/photo-1652451991281-e637ec408bec?q=80&w=2233&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        colorClass: "gocart"
    },
    {
        id: 2,
        name: "Minigolf",
        description: "Hyggelig 18-hullers minigolf bane for hele familien. Tag udfordringen op og se hvem der kan score lavest!",
        price: 75.00,
        duration: 60,
        minAge: 5,
        minHeight: 0,
        availableFrom: "2024-10-01T10:00:00",
        availableTo: "2024-12-31T20:00:00",
        imageUrl: "https://images.unsplash.com/photo-1730198439547-413dc40624bf?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        colorClass: "minigolf"
    },
    {
        id: 3,
        name: "Paintball",
        description: "Taktisk teamwork og action! Kæmp mod dine venner i et spændende paintball-slag på vores udendørs bane.",
        price: 200.00,
        duration: 90,
        minAge: 16,
        minHeight: 0,
        availableFrom: "2024-10-01T10:00:00",
        availableTo: "2024-12-31T18:00:00",
        imageUrl: "https://media.istockphoto.com/id/115702444/photo/paintball-player-under-gunfire.jpg?s=1024x1024&w=is&k=20&c=SNGKnB1zfOLCNbyu9ezKz0fGlLsmx1b8gfqRCaqLwbQ=",
        colorClass: "paintball"
    },
    {
        id: 4,
        name: "Sumo Wrestling",
        description: "Tag sumo-drakten på og udfordre dine venner i morsom sumo brydning. Garanteret sjovt for alle!",
        price: 100.00,
        duration: 20,
        minAge: 8,
        minHeight: 120,
        availableFrom: "2024-10-01T10:00:00",
        availableTo: "2024-12-31T20:00:00",
        imageUrl: "https://thumbs.dreamstime.com/b/sumo-wrestlers-4836066.jpg",
        colorClass: "sumo"
    }
];

// HJÆLPEFUNKTIONER

// Formaterer dato/tid til dansk format
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'Ikke angivet';
    const date = new Date(dateTimeString);
    return date.toLocaleString('da-DK', { //** define how to display the date */
        day: '2-digit',
        month: 'short',
        year: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit'
    });
}

// Formaterer pris til danske kroner
function formatPrice(price) {
    return new Intl.NumberFormat('da-DK', {  /** creater a formatter object, that defines how to display the price **/
        style: 'currency',                   /** a style sed for displayinf valuta */
        currency: 'DKK',                     /** adds kr, after definyng that the currency is danish **/
        minimumFractionDigits: 2.            /** defines number of decimals so the output looked like 10.00kr for example, instead of 10kr **/
    }).format(price);                       /** call a built-in function on the formatter object to change the price to a string, after it been formatted the way we defined **/
}

// OPRET AKTIVITETSKORT
function createActivityCard(activity) {
    return `
        <div class="activity-card">
            <div class="activity-image ${activity.colorClass}">
                <img src="${activity.imageUrl}" alt="${activity.name}" class="activity-img">
            </div>
            <div class="activity-content">
                <h3 class="activity-title">${activity.name}</h3>
                <p class="activity-description">${activity.description}</p>
                
                <div class="activity-details">
                    <div class="detail-item">
                        <span class="detail-label">Varighed:</span>
                        <span class="detail-value">${activity.duration} min</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Min. alder:</span>
                        <span class="detail-value">${activity.minAge} år</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Min. højde:</span>
                        <span class="detail-value">${activity.minHeight > 0 ? activity.minHeight + ' cm' : 'Ingen krav'}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Tilgængelig fra:</span>
                        <span class="detail-value">${formatDateTime(activity.availableFrom)}</span>
                    </div>
                </div>
                
                <div class="price-tag">${formatPrice(activity.price)}</div>
            </div>
        </div>
    `;
}

// VIS ALLE AKTIVITETER
function displayActivities() {
    const container = document.getElementById('activities-container');
    
    // Hvis ingen aktiviteter findes, for eksempel hvis bruger sletter alle
    if (activities.length === 0) {
        container.innerHTML = '<div class="loading">Ingen aktiviteter tilgængelige endnu.</div>';
        return;
    }
    
    // Opret HTML for alle aktiviteter
    container.innerHTML = activities
        .map(activity => createActivityCard(activity))
        .join(''); //** all elements from array become one single string without anything that devide them apart        
        //              done so that we could add it to innerHTML that expects single string **/
}

// START NÅR SIDEN ER KLAR
document.addEventListener('DOMContentLoaded', displayActivities);