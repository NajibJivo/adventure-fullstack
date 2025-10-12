// Tjek URL parametre for fejl/logout beskeder
const urlParams = new URLSearchParams(window.location.search);

if (urlParams.get('error')) {
    const errorMsg = document.getElementById('error-msg');
    errorMsg.textContent = '❌ Forkert brugernavn eller adgangskode';
    errorMsg.style.display = 'block';
}

if (urlParams.get('logout')) {
    const logoutMsg = document.getElementById('logout-msg');
    logoutMsg.textContent = '✅ Du er nu logget ud';
    logoutMsg.style.display = 'block';
}
