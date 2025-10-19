const API_BASE = '/api/product';
let allProducts = [];
let isOwner = false; // Holder styr på om brugeren er admin

// Kører når siden loader
document.addEventListener('DOMContentLoaded', () => {
    checkUserRole(); // Tjek brugerens rolle først
    loadProducts();
    setupForm();
});

// Tjekker om brugeren er OWNER eller CUSTOMER
async function checkUserRole() {
    try {
        const response = await fetch('/api/auth/current');
        const user = await response.json();
        isOwner = user.role === 'OWNER';

        // Skjul "Tilføj Produkt" knap hvis ikke owner
        if (!isOwner) {
            const addButton = document.querySelector('.btn-primary');
            if (addButton) addButton.style.display = 'none';
        }
    } catch (error) {
        console.error('Kunne ikke hente brugerrolle:', error);
    }
}

// Henter alle produkter fra API
async function loadProducts() {
    try {
        const response = await fetch(API_BASE);
        const data = await response.json();
        allProducts = data.content || data; // Håndter både Page<T> og List<T>
        displayProducts(allProducts);
    } catch (error) {
        console.error('Fejl:', error);
        document.getElementById('products-container').innerHTML = `
            <div class="error-message">
                Kunne ikke indlæse produkter. Prøv at genindlæse siden.
            </div>
        `;
    }
}

// Viser produkterne på siden
function displayProducts(products) {
    const container = document.getElementById('products-container');

    if (!products || products.length === 0) {
        container.innerHTML = '<div class="loading">Ingen produkter fundet</div>';
        return;
    }

    container.innerHTML = products
        .map(product => createProductCard(product))
        .join('');
}

// Laver HTML for et enkelt produkt-kort
function createProductCard(product) {
    // Viser forskellige knapper afhængig af brugerrolle
    const actionsHTML = isOwner ? `
        <div class="product-actions">
            <button class="btn btn-small btn-edit" onclick="editProduct(${product.id})">
                Rediger
            </button>
            <button class="btn btn-small btn-delete" onclick="deleteProduct(${product.id})">
                Slet
            </button>
        </div>
    ` : `
        <div class="product-actions">
            <button class="btn btn-small btn-primary" onclick="buyProduct(${product.id}, '${product.name}')">
                Køb
            </button>
        </div>
    `;

    return `
        <div class="product-card">
            <div class="product-header">
                <h3>${product.name}</h3>
            </div>
            
            <div class="product-info">
                <div class="info-row">
                    <span>Pris:</span>
                    <strong>${product.price.toFixed(2)} kr</strong>
                </div>
            </div>
            
            ${actionsHTML}
        </div>
    `;
}

// Køb funktion for kunder
async function buyProduct(productId, productName) {
    const quantity = prompt(`Hvor mange ${productName} vil du købe?`, '1');
    if (!quantity || quantity <= 0) return;

    try {
        const response = await fetch('/api/sales', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                productId: productId,
                quantity: parseInt(quantity)
            })
        });

        if (!response.ok) throw new Error('Køb fejlede');

        alert('Produkt købt!');
        loadProducts(); // Genindlæs produkter
    } catch (error) {
        console.error('Fejl:', error);
        alert('Kunne ikke gennemføre køb: ' + error.message);
    }
}

// Åbner formular til at tilføje nyt produkt
function showAddProductForm() {
    if (!isOwner) return; // Sikkerhed: kun admin

    document.getElementById('modal-title').textContent = 'Tilføj Produkt';
    document.getElementById('product-form').reset();
    document.getElementById('product-id').value = '';
    document.getElementById('product-modal').style.display = 'block';
}

// Åbner formular til at redigere eksisterende produkt
function editProduct(id) {
    if (!isOwner) return; // Sikkerhed: kun admin

    const product = allProducts.find(p => p.id === id);
    if (!product) return;

    document.getElementById('modal-title').textContent = 'Rediger Produkt';
    document.getElementById('product-id').value = product.id;
    document.getElementById('product-name').value = product.name;
    document.getElementById('product-price').value = product.price;
    document.getElementById('product-modal').style.display = 'block';
}

// Sletter et produkt
async function deleteProduct(id) {
    if (!isOwner) return; // Sikkerhed: kun admin

    if (!confirm('Er du sikker på at du vil slette dette produkt?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Kunne ikke slette');
        }

        alert('Produkt slettet!');
        loadProducts();
    } catch (error) {
        alert('Fejl ved sletning af produkt');
    }
}

// Sætter form submit handler op
function setupForm() {
    document.getElementById('product-form').addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!isOwner) return; // Sikkerhed: kun admin

        const id = document.getElementById('product-id').value;
        const productData = {
            name: document.getElementById('product-name').value,
            price: parseFloat(document.getElementById('product-price').value),
            isActive: true
        };

        try {
            const url = id ? `${API_BASE}/${id}` : API_BASE;
            const method = id ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Kunne ikke gemme');
            }

            alert('Produkt gemt!');
            closeModal();
            loadProducts();
        } catch (error) {
            alert('Fejl: ' + error.message);
        }
    });
}

// Lukker modal popup
function closeModal() {
    document.getElementById('product-modal').style.display = 'none';
}

// Luk modal hvis man klikker udenfor den
window.onclick = function(event) {
    const modal = document.getElementById('product-modal');
    if (event.target === modal) {
        closeModal();
    }
}
