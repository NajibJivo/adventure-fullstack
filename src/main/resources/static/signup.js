const form = document.getElementById('signup-form');
const errorMsg = document.getElementById('error-msg');
const successMsg = document.getElementById('success-msg');
const submitBtn = document.getElementById('submit-btn');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Skjul beskeder
    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    // Hent formdata
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Valider passwords matcher
    if (password !== confirmPassword) {
        showError('Adgangskoderne matcher ikke');
        return;
    }

    // Forbered data
    const signupData = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value || null,
        username: document.getElementById('username').value,
        password: password
    };

    // Disable knap
    submitBtn.disabled = true;
    submitBtn.textContent = 'Opretter...';

    try {
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(signupData)
        });

        if (response.ok) {
            showSuccess('Bruger oprettet! Omdirigerer til login...');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 2000);
        } else {
            const errorText = await response.text();
            showError(errorText || 'Der opstod en fejl');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Opret bruger';
        }
    } catch (error) {
        showError('Kunne ikke oprette bruger. Prøv igen.');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Opret bruger';
    }
});

function showError(message) {
    errorMsg.textContent = '❌ ' + message;
    errorMsg.style.display = 'block';
}

function showSuccess(message) {
    successMsg.textContent = '✅ ' + message;
    successMsg.style.display = 'block';
}
