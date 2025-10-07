// Konstant med base URL til API'et
// Bruges i alle fetch-kald for at undgå gentagelse
const API_BASE = '/api/products';

// Global variabel der holder alle produkter fra databasen
// Bruges til filtrering uden at kalde API'et igen
let allProducts = [];

// DOMContentLoaded event fires når HTML'en er loaded
// Sikrer at vi ikke prøver at tilgå elementer der ikke eksisterer endnu
document.addEventListener('DOMContentLoaded', () => {
    // Hent produkter fra backend når siden loader
    loadProducts();
    // Opsæt formular til at lytte efter submit
    setupForm();
});

// Asynkron funktion til at hente produkter fra API
// 'async' tillader brug af 'await' inde i funktionen
async function loadProducts() {
    // try-catch håndterer fejl (fx hvis serveren er nede)
    try {
        // fetch sender HTTP GET request til /api/products
        // await venter på svar før koden fortsætter
        const response = await fetch(API_BASE);

        // .json() konverterer JSON-string til JavaScript array
        // await fordi .json() også er asynkron
        allProducts = await response.json();

        // Vis produkterne på siden
        displayProducts(allProducts);
    } catch (error) {
        // Hvis noget går galt (fx ingen internet), vis fejlbesked
        // getElementById finder HTML-elementet med id="products-container"
        // innerHTML erstatter alt indhold i elementet
        document.getElementById('products-container').innerHTML = `
            <div class="error-message">
                Kunne ikke indlæse produkter. 
                <button class="btn" onclick="initTestData()">Indlæs testdata</button>
            </div>
        `;
    }
}

// Funktion til at vise produkter på siden
function displayProducts(products) {
    // Find containeren hvor produkter skal vises
    const container = document.getElementById('products-container');

    // Tjek om der er nogen produkter at vise
    if (products.length === 0) {
        // Hvis arrayet er tomt, vis besked
        container.innerHTML = '<div class="loading">Ingen produkter fundet</div>';
        // return stopper funktionen her (springer resten over)
        return;
    }

    // .map() gennemgår hvert produkt og laver HTML
    // createProductCard() laver HTML-string for ét produkt
    // .join('') kombinerer alle HTML-strings til én string
    container.innerHTML = products
        .map(product => createProductCard(product))
        .join('');
}

// Funktion der laver HTML for ét produkt-kort
function createProductCard(product) {
    // Template literal (backticks `) tillader multi-line strings og ${} for variabler
    return `
        <div class="product-card">
            <div class="product-header">
                <!-- Viser produkt navn -->
                <h3>${product.name}</h3>
                
                <!-- Category badge med farve baseret på kategori -->
                <!-- .toLowerCase() konverterer "T_SHIRT" til "t_shirt" for CSS class -->
                <span class="category-badge ${product.category.toLowerCase()}">
                    <!-- getCategoryName() oversætter "T_SHIRT" til "T-shirt" -->
                    ${getCategoryName(product.category)}
                </span>
            </div>
            
            <!-- Beskrivelse - || '' betyder vis tom string hvis description er null -->
            <p class="product-description">${product.description || ''}</p>
            
            <div class="product-info">
                <!-- Pris med 2 decimaler -->
                <div class="info-row">
                    <span>Pris:</span>
                    <!-- .toFixed(2) sikrer 2 decimaler: 149.00 -->
                    <strong>${product.price.toFixed(2)} kr</strong>
                </div>
                
                <!-- Lagerbeholdning -->
                <div class="info-row">
                    <span>Lager:</span>
                    <strong>${product.stockQuantity} stk</strong>
                </div>
                
                <!-- Størrelse - kun vist hvis product.size har en værdi -->
                <!-- Ternary operator: betingelse ? hvis_sand : hvis_falsk -->
                <!-- ? tjekker om product.size er truthy (ikke null/undefined/'') -->
                ${product.size ? `
                    <div class="info-row">
                        <span>Størrelse:</span>
                        <strong>${product.size}</strong>
                    </div>
                ` : ''}
            </div>
            
            <!-- Action knapper -->
            <div class="product-actions">
                <!-- Rediger-knap - kalder editProduct() med produktets ID -->
                <button class="btn btn-small btn-edit" onclick="editProduct(${product.id})">
                    Rediger
                </button>
                
                <!-- Slet-knap - kalder deleteProduct() med produktets ID -->
                <button class="btn btn-small btn-delete" onclick="deleteProduct(${product.id})">
                    Slet
                </button>
            </div>
        </div>
    `;
}

// Funktion til at filtrere produkter efter kategori
function filterProducts(category) {
    // Fjern 'active' class fra alle filter-knapper
    // querySelectorAll finder alle elementer med class="filter-btn"
    // forEach gennemgår hvert element
    document.querySelectorAll('.filter-btn').forEach(btn => {
        // classList.remove fjerner CSS-klassen 'active'
        btn.classList.remove('active');
    });

    // Tilføj 'active' class til den knap der blev klikket
    // event.target er det element der triggede eventet (den klikkede knap)
    event.target.classList.add('active');

    // Filtrer produkter og vis dem
    if (category === 'ALL') {
        // Vis alle produkter
        displayProducts(allProducts);
    } else {
        // .filter() laver et nyt array med kun produkter der matcher
        // p => p.category === category betyder: behold produkter hvor kategorien matcher
        const filtered = allProducts.filter(p => p.category === category);
        // Vis de filtrerede produkter
        displayProducts(filtered);
    }
}

// Vis formular til at tilføje nyt produkt
function showAddProductForm() {
    // Ændre modal titel til "Tilføj Produkt"
    // textContent sætter tekst-indholdet af elementet
    document.getElementById('modal-title').textContent = 'Tilføj Produkt';

    // .reset() tømmer alle felter i formularen
    document.getElementById('product-form').reset();

    // Sæt skjult ID-felt til tom string (nyt produkt har intet ID)
    document.getElementById('product-id').value = '';

    // Gør modal synlig ved at ændre CSS display property
    // CSS har normalt display: none (skjult)
    document.getElementById('product-modal').style.display = 'block';
}

// Funktion til at redigere eksisterende produkt
function editProduct(id) {
    // .find() søger i array og returnerer første match
    // p => p.id === id betyder: find produkt hvor ID matcher
    const product = allProducts.find(p => p.id === id);

    // Hvis produktet ikke findes, stop funktionen
    if (!product) return;

    // Ændre modal titel til "Rediger Produkt"
    document.getElementById('modal-title').textContent = 'Rediger Produkt';

    // Udfyld alle form-felter med produktets eksisterende data
    // .value sætter værdien af input-felter
    document.getElementById('product-id').value = product.id;
    document.getElementById('product-name').value = product.name;
    // || '' betyder: brug tom string hvis description er null
    document.getElementById('product-description').value = product.description || '';
    document.getElementById('product-price').value = product.price;
    document.getElementById('product-category').value = product.category;
    document.getElementById('product-stock').value = product.stockQuantity;
    document.getElementById('product-size').value = product.size || '';

    // Vis modal popup
    document.getElementById('product-modal').style.display = 'block';
}

// Asynkron funktion til at slette produkt
async function deleteProduct(id) {
    // confirm() viser en popup med OK/Cancel knapper
    // Returnerer true hvis bruger klikker OK, false hvis Cancel
    if (!confirm('Er du sikker på at du vil slette dette produkt?')) {
        // Hvis bruger klikker Cancel, stop funktionen
        return;
    }

    try {
        // Send HTTP DELETE request til /api/products/{id}
        // Template literal: `${API_BASE}/${id}` bliver fx "/api/products/5"
        const response = await fetch(`${API_BASE}/${id}`, {
            // method specificerer HTTP-metoden (GET er default)
            method: 'DELETE'
        });

        // Tjek om request var succesfuld
        // response.ok er true for status 200-299
        if (!response.ok) {
            // Kast en fejl hvis request fejlede
            throw new Error('Kunne ikke slette');
        }

        // Vis succesbesked til bruger
        alert('Produkt slettet!');

        // Genindlæs produkter fra backend for at opdatere visningen
        loadProducts();
    } catch (error) {
        // Vis fejlbesked hvis noget gik galt
        alert('Fejl ved sletning af produkt');
    }
}

// Funktion til at opsætte formular submit-håndtering
function setupForm() {
    // Find formularen og tilføj event listener
    // addEventListener lytter efter 'submit' event (når bruger klikker "Gem")
    document.getElementById('product-form').addEventListener('submit', async (e) => {
        // e.preventDefault() forhindrer standard form submit (page reload)
        e.preventDefault();

        // Hent ID fra skjult felt
        // Tomt hvis nyt produkt, indeholder ID hvis redigering
        const id = document.getElementById('product-id').value;

        // Opret objekt med alle produkt-data fra formularen
        const productData = {
            // Hent værdier fra input-felter
            name: document.getElementById('product-name').value,
            description: document.getElementById('product-description').value,
            // parseFloat konverterer string "149.00" til number 149.00
            price: parseFloat(document.getElementById('product-price').value),
            category: document.getElementById('product-category').value,
            // parseInt konverterer string "50" til number 50
            stockQuantity: parseInt(document.getElementById('product-stock').value),
            // || null betyder: brug null hvis feltet er tomt
            size: document.getElementById('product-size').value || null
        };

        try {
            // Bestem URL og HTTP-metode baseret på om det er nyt eller eksisterende
            // Ternary operator: betingelse ? hvis_sand : hvis_falsk
            // Hvis id har værdi: opdater eksisterende (PUT), ellers: opret ny (POST)
            const url = id ? `${API_BASE}/${id}` : API_BASE;
            const method = id ? 'PUT' : 'POST';

            // Send HTTP request til backend
            const response = await fetch(url, {
                method: method,
                // headers fortæller serveren at vi sender JSON
                headers: { 'Content-Type': 'application/json' },
                // JSON.stringify konverterer JavaScript object til JSON-string
                body: JSON.stringify(productData)
            });

            // Tjek om request var succesfuld
            if (!response.ok) {
                throw new Error('Kunne ikke gemme');
            }

            // Vis succesbesked
            alert('Produkt gemt!');

            // Luk modal popup
            closeModal();

            // Genindlæs produkter fra backend
            loadProducts();
        } catch (error) {
            // Vis fejlbesked
            alert('Fejl ved gemning af produkt');
        }
    });
}

// Funktion til at lukke modal popup
function closeModal() {
    // Skjul modal ved at sætte display til 'none'
    document.getElementById('product-modal').style.display = 'none';
}

// Hjælpefunktion til at oversætte kategori-koder til læsbare navne
function getCategoryName(category) {
    // Række af if-statements der returnerer dansk navn
    if (category === 'T_SHIRT') return 'T-shirt';
    if (category === 'SNACK') return 'Slik';
    if (category === 'SODA') return 'Sodavand';
    // Hvis ingen match, returner den originale værdi
    return category;
}

// Event listener for klik udenfor modal
// window.onclick lytter efter ALLE klik på hele siden
window.onclick = function(event) {
    // Find modal elementet
    const modal = document.getElementById('product-modal');

    // Tjek om det klikkede element ER modal'en (baggrunden, ikke indholdet)
    if (event.target === modal) {
        // Luk modal hvis bruger klikker på baggrunden
        closeModal();
    }
}