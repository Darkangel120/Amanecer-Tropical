const API_BASE_URL = 'http://localhost:8080/api';
let selectedProfilePicture = null;

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
    console.log('Profile form found:', profileForm);
    if (profileForm) {
        profileForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            console.log('Form submitted');
            await updateUserProfile();
        });
    }

    // Profile picture change functionality
    const changePictureBtn = document.getElementById('change-picture-btn');
    const profilePictureInput = document.getElementById('profile-picture-input');
    const profilePictureContainer = document.querySelector('.profile-picture-container');

    // Create profile picture element dynamically if it doesn't exist
    let profilePicture = document.getElementById('profile-picture');
    if (!profilePicture) {
        profilePicture = document.createElement('img');
        profilePicture.id = 'profile-picture';
        profilePicture.className = 'profile-picture';
        profilePicture.alt = 'Foto de Perfil';
        profilePicture.src = '';
        profilePictureContainer.insertBefore(profilePicture, changePictureBtn);
    }

    if (changePictureBtn && profilePictureInput) {
        changePictureBtn.addEventListener('click', function() {
            profilePictureInput.click();
        });

        profilePictureInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                // Resize image to reduce base64 size
                resizeImage(file, 200, 200, function(resizedDataUrl) {
                    profilePicture.src = resizedDataUrl;
                    selectedProfilePicture = resizedDataUrl; // Store resized base64 for upload
                });
            }
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
    const profilePicture = document.getElementById('profile-picture');

    // Map backend field names to form field names
    if (nameInput) nameInput.value = user.nombre || '';
    if (emailInput) emailInput.value = user.correoElectronico || '';
    if (phoneInput) phoneInput.value = user.telefono || '';
    if (birthdateInput) birthdateInput.value = user.fechaNacimiento ? new Date(user.fechaNacimiento).toISOString().split('T')[0] : '';
    if (genderInput) genderInput.value = user.genero || '';
    if (nationalityInput) nationalityInput.value = user.nacionalidad || '';
    if (addressInput) addressInput.value = user.direccion || '';
    if (cityInput) cityInput.value = user.ciudad || '';
    if (stateInput) stateInput.value = user.estado || '';
    if (cedulaInput) cedulaInput.value = user.cedula || '';
    if (passportInput) passportInput.value = user.pasaporte || '';
    if (passportExpiryInput) passportExpiryInput.value = user.fechaExpiracionPasaporte ? new Date(user.fechaExpiracionPasaporte).toISOString().split('T')[0] : '';
    if (emergencyNameInput) emergencyNameInput.value = user.nombreEmergencia || '';
    if (emergencyPhoneInput) emergencyPhoneInput.value = user.telefonoEmergencia || '';
    if (emergencyRelationshipInput) emergencyRelationshipInput.value = user.relacionEmergencia || '';
    if (travelStyleInput) travelStyleInput.value = user.estiloViaje || '';
    if (dietaryRestrictionsInput) dietaryRestrictionsInput.value = user.restriccionesDieteticas || '';
    if (specialNeedsInput) specialNeedsInput.value = user.necesidadesEspeciales || '';
    
    if (profilePicture) {
        if (user.fotoPerfil && user.fotoPerfil.startsWith('/uploads/')) {
            // Profile picture is a file path from backend
            profilePicture.src = `http://localhost:8080${user.fotoPerfil}`;
            profilePicture.onerror = function() {
                // If profile picture fails to load, show default
                profilePicture.src = '../assets/img/default-profile.png';
            };
        } else if (user.fotoPerfil && user.fotoPerfil.startsWith('data:image')) {
            // Profile picture is base64 (fallback for old data)
            profilePicture.src = user.fotoPerfil;
        } else {
            // No profile picture, show default
            profilePicture.src = '../assets/img/default-profile.png';
        }
    }
}

async function updateUserProfile() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    const formData = new FormData(document.querySelector('.profile-form'));

    // Helper function to get value or null if empty
    const getValueOrNull = (key) => {
        const value = formData.get(key);
        return value && value.trim() !== '' ? value : null;
    };

    // Check required fields (match backend validation)
    const requiredFields = [
        'name', 'email', 'phone', 'birthdate', 'gender', 
        'nationality', 'address', 'city', 'state', 'cedula'
    ];
    
    for (const field of requiredFields) {
        if (!formData.get(field) || formData.get(field).trim() === '') {
            alert(`El campo ${getFieldLabel(field)} es obligatorio.`);
            return;
        }
    }

    // Map form fields to backend field names
    const updatedUser = {
        nombre: formData.get('name'),
        correoElectronico: formData.get('email'),
        telefono: formData.get('phone'),
        fechaNacimiento: formData.get('birthdate'),
        genero: formData.get('gender'),
        nacionalidad: formData.get('nationality'),
        direccion: formData.get('address'),
        ciudad: formData.get('city'),
        estado: formData.get('state'),
        cedula: formData.get('cedula'),
        pasaporte: getValueOrNull('passport'),
        fechaExpiracionPasaporte: getValueOrNull('passport-expiry'),
        nombreEmergencia: getValueOrNull('emergency-name'),
        telefonoEmergencia: getValueOrNull('emergency-phone'),
        relacionEmergencia: getValueOrNull('emergency-relationship'),
        estiloViaje: getValueOrNull('travel-style'),
        restriccionesDieteticas: getValueOrNull('dietary-restrictions'),
        necesidadesEspeciales: getValueOrNull('special-needs')
    };

    // Only include profilePicture if a new one was selected
    if (selectedProfilePicture) {
        updatedUser.fotoPerfil = selectedProfilePicture;
    }

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
            
            // Update the profile picture display if it was changed
            if (updatedUserData.fotoPerfil) {
                const profilePicture = document.getElementById('profile-picture');
                if (profilePicture) {
                    if (updatedUserData.fotoPerfil.startsWith('/uploads/')) {
                        profilePicture.src = `http://localhost:8080${updatedUserData.fotoPerfil}`;
                        profilePicture.onerror = function() {
                            profilePicture.src = '../assets/img/default-profile.png';
                        };
                    } else if (updatedUserData.fotoPerfil.startsWith('data:image')) {
                        profilePicture.src = updatedUserData.fotoPerfil;
                    } else {
                        profilePicture.src = '../assets/img/default-profile.png';
                    }
                }
            }
            
            alert('Perfil actualizado exitosamente');
            // Reset selected picture
            selectedProfilePicture = null;
        } else {
            const error = await response.text();
            alert('Error al actualizar el perfil: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}

// Helper function to get field labels
function getFieldLabel(fieldName) {
    const labels = {
        'name': 'Nombre',
        'email': 'Correo Electrónico',
        'phone': 'Teléfono',
        'birthdate': 'Fecha de Nacimiento',
        'gender': 'Género',
        'nationality': 'Nacionalidad',
        'address': 'Dirección',
        'city': 'Ciudad',
        'state': 'Estado',
        'cedula': 'Cédula'
    };
    return labels[fieldName] || fieldName;
}

// Function to resize image
function resizeImage(file, maxWidth, maxHeight, callback) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();

    img.onload = function() {
        // Calculate new dimensions
        let { width, height } = img;

        if (width > height) {
            if (width > maxWidth) {
                height = (height * maxWidth) / width;
                width = maxWidth;
            }
        } else {
            if (height > maxHeight) {
                width = (width * maxHeight) / height;
                height = maxHeight;
            }
        }

        canvas.width = width;
        canvas.height = height;

        // Draw resized image
        ctx.drawImage(img, 0, 0, width, height);

        // Convert to base64
        const dataUrl = canvas.toDataURL('image/jpeg', 0.8);
        callback(dataUrl);
    };

    img.src = URL.createObjectURL(file);
}