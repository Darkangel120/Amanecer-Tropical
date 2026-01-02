// Profile JavaScript
const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Navbar toggle for mobile
    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    burger.addEventListener('click', () => {
        nav.classList.toggle('nav-active');
        burger.classList.toggle('toggle');
    });

    // Animate nav links
    navLinks.forEach((link, index) => {
        link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
    });

    // Load user profile
    loadUserProfile();

    // Profile form submission
    const profileForm = document.querySelector('.profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await updateUserProfile();
        });
    }

    // Logout functionality
    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../index.html';
        });
    }
});

async function loadUserProfile() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    try {
        const response = await fetch(`${API_BASE_URL}/users/${user.id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const userData = await response.json();
            populateProfileForm(userData);
        } else {
            console.error('Error loading user profile');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function populateProfileForm(user) {
    const nameInput = document.querySelector('#name');
    const emailInput = document.querySelector('#email');
    const phoneInput = document.querySelector('#phone');
    const birthdateInput = document.querySelector('#birthdate');
    const genderInput = document.querySelector('#gender');
    const nationalityInput = document.querySelector('#nationality');
    const addressInput = document.querySelector('#address');
    const cityInput = document.querySelector('#city');
    const stateInput = document.querySelector('#state');
    const cedulaInput = document.querySelector('#cedula');
    const passportInput = document.querySelector('#passport');
    const passportExpiryInput = document.querySelector('#passport-expiry');
    const emergencyNameInput = document.querySelector('#emergency-name');
    const emergencyPhoneInput = document.querySelector('#emergency-phone');
    const emergencyRelationshipInput = document.querySelector('#emergency-relationship');
    const travelStyleInput = document.querySelector('#travel-style');
    const dietaryRestrictionsInput = document.querySelector('#dietary-restrictions');
    const specialNeedsInput = document.querySelector('#special-needs');

    if (nameInput) nameInput.value = user.name || '';
    if (emailInput) emailInput.value = user.email || '';
    if (phoneInput) phoneInput.value = user.phone || '';
    if (birthdateInput) birthdateInput.value = user.birthdate ? new Date(user.birthdate).toISOString().split('T')[0] : '';
    if (genderInput) genderInput.value = user.gender || '';
    if (nationalityInput) nationalityInput.value = user.nationality || '';
    if (addressInput) addressInput.value = user.address || '';
    if (cityInput) cityInput.value = user.city || '';
    if (stateInput) stateInput.value = user.state || '';
    if (cedulaInput) cedulaInput.value = user.cedula || '';
    if (passportInput) passportInput.value = user.passport || '';
    if (passportExpiryInput) passportExpiryInput.value = user.passportExpiry ? new Date(user.passportExpiry).toISOString().split('T')[0] : '';
    if (emergencyNameInput) emergencyNameInput.value = user.emergencyName || '';
    if (emergencyPhoneInput) emergencyPhoneInput.value = user.emergencyPhone || '';
    if (emergencyRelationshipInput) emergencyRelationshipInput.value = user.emergencyRelationship || '';
    if (travelStyleInput) travelStyleInput.value = user.travelStyle || '';
    if (dietaryRestrictionsInput) dietaryRestrictionsInput.value = user.dietaryRestrictions || '';
    if (specialNeedsInput) specialNeedsInput.value = user.specialNeeds || '';
}

async function updateUserProfile() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    const formData = new FormData(document.querySelector('.profile-form'));
    const updatedUser = {
        name: formData.get('name'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        birthdate: formData.get('birthdate'),
        gender: formData.get('gender'),
        nationality: formData.get('nationality'),
        address: formData.get('address'),
        city: formData.get('city'),
        state: formData.get('state'),
        cedula: formData.get('cedula'),
        passport: formData.get('passport'),
        passportExpiry: formData.get('passport-expiry'),
        emergencyName: formData.get('emergency-name'),
        emergencyPhone: formData.get('emergency-phone'),
        emergencyRelationship: formData.get('emergency-relationship'),
        travelStyle: formData.get('travel-style'),
        dietaryRestrictions: formData.get('dietary-restrictions'),
        specialNeeds: formData.get('special-needs')
    };

    try {
        const response = await fetch(`${API_BASE_URL}/users/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(updatedUser)
        });

        if (response.ok) {
            const updatedUserData = await response.json();
            localStorage.setItem('user', JSON.stringify(updatedUserData));
            alert('Perfil actualizado exitosamente');
        } else {
            const error = await response.text();
            alert('Error al actualizar el perfil: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}
