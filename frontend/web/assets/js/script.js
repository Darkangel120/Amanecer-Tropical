// Toast notification system
function showToast(message, type = 'info', duration = 4000) {
    // Create toast container if it doesn't exist
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    // Set icon based on type
    const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
    };

    toast.innerHTML = `
        <i class="toast-icon ${icons[type] || icons.info}"></i>
        <div class="toast-content">${message}</div>
        <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
    `;

    // Add to container
    container.appendChild(toast);

    // Trigger animation
    setTimeout(() => toast.classList.add('show'), 10);

    // Auto remove after duration
    const removeToast = () => {
        toast.classList.add('fade-out');
        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
        }, 300);
    };

    setTimeout(removeToast, duration);

    // Allow manual removal on click
    toast.addEventListener('click', removeToast);
}

const navSlide = () => {
    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    burger.addEventListener('click', () => {
        nav.classList.toggle('nav-active');

        navLinks.forEach((link, index) => {
            if (link.style.animation) {
                link.style.animation = '';
            } else {
                link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
            }
        });

        burger.classList.toggle('toggle');
    });
}

const carousel = () => {
    const slides = document.querySelectorAll('.carousel-slide');
    const indicators = document.querySelectorAll('.indicator');
    const tooltip = document.querySelector('.carousel-tooltip');
    let currentSlide = 0;
    let autoSlideInterval;
    let isAutoSliding = true;
    let isTooltipVisible = false;

    const showTooltip = (slide) => {
        const tooltipText = slide.getAttribute('data-tooltip');
        if (tooltip && tooltipText) {
            tooltip.textContent = tooltipText;
            tooltip.style.opacity = '1';
            isTooltipVisible = true;
        }
    }

    const hideTooltip = () => {
        if (tooltip) {
            tooltip.style.opacity = '0';
            isTooltipVisible = false;
        }
    }

    const showSlide = (index) => {
        slides.forEach(slide => slide.classList.remove('active'));
        indicators.forEach(indicator => indicator.classList.remove('active'));

        slides[index].classList.add('active');
        indicators[index].classList.add('active');

        // Update tooltip if visible
        if (isTooltipVisible) {
            showTooltip(slides[index]);
        }

        currentSlide = index;
    }

    const nextSlide = () => {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    const startAutoSlide = () => {
        if (!isAutoSliding) {
            autoSlideInterval = setInterval(nextSlide, 4000);
            isAutoSliding = true;
        }
    }

    const stopAutoSlide = () => {
        if (isAutoSliding) {
            clearInterval(autoSlideInterval);
            isAutoSliding = false;
        }
    }

    // Event listeners for slides (hover and touch)
    slides.forEach((slide, index) => {
        // Desktop hover
        slide.addEventListener('mouseenter', () => {
            showTooltip(slide);
            stopAutoSlide();
        });

        slide.addEventListener('mouseleave', () => {
            hideTooltip();
            setTimeout(() => startAutoSlide(), 2000);
        });

        // Mobile touch
        slide.addEventListener('touchstart', (e) => {
            e.preventDefault();
            showTooltip(slide);
            isTooltipVisible = true;
        });
    });

    // Event listeners for indicators
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => {
            showSlide(index);
            stopAutoSlide();
            setTimeout(() => startAutoSlide(), 2000);
        });
    });

    // Start auto slide
    startAutoSlide();
}

const handleMobileButtons = () => {
    const loginBtn = document.querySelector('.btn-login');
    const registerBtn = document.querySelector('.btn-register');

    const showTooltip = (button) => {
        const tooltipText = button.getAttribute('data-tooltip');
        let tooltipSpan = button.querySelector('.tooltip');
        if (!tooltipSpan) {
            tooltipSpan = document.createElement('span');
            tooltipSpan.className = 'tooltip';
            button.appendChild(tooltipSpan);
        }
        if (tooltipSpan && tooltipText) {
            tooltipSpan.textContent = tooltipText;
        }
        button.classList.add('show-tooltip');
        setTimeout(() => {
            button.classList.remove('show-tooltip');
        }, 2000);
    };

    const isMobile = window.innerWidth <= 768; // Match the CSS breakpoint

    if (isMobile) {
        const handleNavigation = (button, e) => {
            e.preventDefault();
            showTooltip(button);
            setTimeout(() => {
                window.location.href = button.href;
            }, 2000);
        };

        loginBtn.addEventListener('click', (e) => handleNavigation(loginBtn, e));
        registerBtn.addEventListener('click', (e) => handleNavigation(registerBtn, e));

        loginBtn.addEventListener('touchstart', (e) => handleNavigation(loginBtn, e));
        registerBtn.addEventListener('touchstart', (e) => handleNavigation(registerBtn, e));
    }
    // On desktop, allow default link behavior (navigation)
};

const API_BASE_URL = 'http://localhost:8080/api';

// Load destinations and packages on main page
const loadMainPageData = () => {
    if (document.getElementById('destinos-grid')) {
        loadDestinations();
    }
    if (document.getElementById('paquetes-grid')) {
        loadPackages();
    }
};

async function loadDestinations() {
    try {
        const response = await fetch(`${API_BASE_URL}/packages`);
        if (response.ok) {
            const destinations = await response.json();
            displayDestinations(destinations);
        } else {
            console.error('Error al Cargar los Destinos');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayDestinations(destinations) {
    const destinosGrid = document.getElementById('destinos-grid');
    if (!destinosGrid) return;

    destinosGrid.innerHTML = '';

    destinations.slice(0, 4).forEach(destination => {
        const imageUrl = destination.urlImagen || 'assets/img/default-destination.jpg';
        const destinationCard = document.createElement('div');
        destinationCard.className = 'destino-card';
        destinationCard.innerHTML = `
            <img src="${imageUrl}" alt="${destination.nombre}">
            <h3>${destination.nombre}</h3>
            <p>${destination.ubicacion}</p>
            <div class="destino-price">Desde $${destination.precio}</div>
            <a href="user/services.html?destination=${destination.id}" class="btn">Ver Detalles</a>
        `;
        destinosGrid.appendChild(destinationCard);
    });
}

async function loadPackages() {
    try {
        const response = await fetch(`${API_BASE_URL}/packages`);
        if (response.ok) {
            const destinations = await response.json();
            displayPackages(destinations);
        } else {
            console.error('Error al Cargar los Paquetes');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayPackages(destinations) {
    const paquetesGrid = document.getElementById('paquetes-grid');
    if (!paquetesGrid) return;

    paquetesGrid.innerHTML = '';

    destinations.forEach(destination => {
        const imageUrl = destination.urlImagen || 'assets/img/default-destination.jpg';
        const packageCard = document.createElement('div');
        packageCard.className = 'paquete-card';
        packageCard.innerHTML = `
            <img src="${imageUrl}" alt="Paquete ${destination.nombre}">
            <div class="paquete-info">
                <h3>Paquete ${destination.nombre}</h3>
                <p class="location">${destination.ubicacion}</p>
                <p class="category">${destination.categoria}</p>
                <p class="duration">${destination.duracionDias} días</p>
                <div class="paquete-price">$${destination.precio}</div>
                <a href="user/services.html?destination=${destination.id}" class="btn">Reservar Ahora</a>
            </div>
        `;
        paquetesGrid.appendChild(packageCard);
    });
}

// Function to check if user is already logged in and redirect to dashboard
const checkExistingSession = async () => {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    // If no token or user in localStorage, allow login page to load normally
    if (!token || !user) {
        return;
    }

    try {
        // Validate session with backend
        const response = await fetch(`${API_BASE_URL}/auth/validate`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            // Session is valid, redirect to dashboard
            console.log('Active session found, redirecting to dashboard');
            window.location.href = 'dashboard.html';
        } else if (response.status === 401) {
            // Token is invalid, clear session but stay on login page
            console.log('Invalid session, clearing localStorage');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        }
        // For other errors, stay on login page
    } catch (error) {
        // Network error, stay on login page but keep session
        console.warn('Network error during session check, keeping session active');
    }
};

const handleForms = () => {
    const loginForm = document.querySelector('#login-form');
    const registerForm = document.querySelector('#register-form');

    // Check for existing session on login page
    if (loginForm) {
        checkExistingSession();
    }

    // Password toggle functionality
    const togglePassword = document.querySelector('#toggle-password');
    if (togglePassword) {
        togglePassword.addEventListener('click', () => {
            const passwordInput = document.querySelector('#password');
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            togglePassword.classList.toggle('fa-eye');
            togglePassword.classList.toggle('fa-eye-slash');
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.querySelector('#email').value;
            const password = document.querySelector('#password').value;

            try {
                const response = await fetch(`${API_BASE_URL}/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email, password }),
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('user', JSON.stringify(data.user));
                    window.location.href = 'dashboard.html';
                } else {
                    const error = await response.text();
                    showToast('Error en el inicio de sesión: ' + error, 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error de conexión.', 'error');
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);

            // Helper function to get value or null if empty
            const getValueOrNull = (key) => {
                const value = formData.get(key);
                return value && value.trim() !== '' ? value : null;
            };

            // Check required fields
            const requiredFields = ['name', 'email', 'password', 'phone', 'birthdate', 'gender', 'nationality', 'address', 'city', 'state', 'cedula'];
            for (const field of requiredFields) {
                if (!formData.get(field) || formData.get(field).trim() === '') {
                    showToast(`El campo ${field} es obligatorio.`, 'warning');
                    return;
                }
            }

            const userData = {
                nombre: formData.get('name'),
                email: formData.get('email'),
                password: formData.get('password'),
                telefono: formData.get('phone'),
                fechaNacimiento: formData.get('birthdate'),
                genero: formData.get('gender'),
                nacionalidad: formData.get('nationality'),
                direccion: formData.get('address'),
                ciudad: formData.get('city'),
                estado: formData.get('estado'),
                cedula: formData.get('cedula'),
                rol: 'USUARIO'
            };

            try {
                const response = await fetch(`${API_BASE_URL}/auth/register`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(userData),
                });

                if (response.ok) {
                    showToast('Registro exitoso. Ahora puedes iniciar sesión.', 'success');
                    window.location.href = 'login.html';
                } else {
                    const error = await response.text();
                    showToast('Error en el registro: ' + error, 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error de conexión.', 'error');
            }
        });
    }
};

const handleMultiStepForm = () => {
    const registerForm = document.querySelector('#register-form');
    if (!registerForm) return;

    const formSections = document.querySelectorAll('.form-section');
    const stepIndicators = document.querySelectorAll('.step');
    const prevBtn = document.querySelector('#prev-btn');
    const nextBtn = document.querySelector('#next-btn');
    const submitBtn = document.querySelector('#submit-btn');

    let currentStep = 0;

    const showStep = (stepIndex) => {
        // Hide all sections
        formSections.forEach(section => section.classList.remove('active'));

        // Show current section
        formSections[stepIndex].classList.add('active');

        // Update step indicators
        stepIndicators.forEach((indicator, index) => {
            indicator.classList.remove('active', 'completed');
            if (index < stepIndex) {
                indicator.classList.add('completed');
            } else if (index === stepIndex) {
                indicator.classList.add('active');
            }
        });

        // Update buttons
        prevBtn.style.display = stepIndex === 0 ? 'none' : 'inline-block';
        nextBtn.style.display = stepIndex === formSections.length - 1 ? 'none' : 'inline-block';
        submitBtn.style.display = stepIndex === formSections.length - 1 ? 'inline-block' : 'none';
    };

    const validateStep = (stepIndex) => {
        const currentSection = formSections[stepIndex];
        const requiredFields = currentSection.querySelectorAll('input[required], select[required]');
        let isValid = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.style.borderColor = '#ff6b6b';
                isValid = false;
            } else {
                field.style.borderColor = '#ddd';
            }
        });

        return isValid;
    };

    nextBtn.addEventListener('click', () => {
        if (validateStep(currentStep)) {
            if (currentStep < formSections.length - 1) {
                currentStep++;
                showStep(currentStep);
            }
        } else {
            showToast('Por favor, complete todos los campos requeridos antes de continuar.', 'warning');
        }
    });

    prevBtn.addEventListener('click', () => {
        if (currentStep > 0) {
            currentStep--;
            showStep(currentStep);
        }
    });

    // Initialize first step
    showStep(currentStep);
};

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'block';
    }
}

const handleModal = () => {
    // Terms Modal
    const termsModal = document.getElementById('terms-modal');
    const termsLink = document.getElementById('terms-link');
    const termsClose = document.querySelector('#terms-modal .close');
    const declineTerms = document.getElementById('decline-terms');
    const acceptTerms = document.getElementById('accept-terms');

    // Privacy Modal
    const privacyModal = document.getElementById('privacy-modal');
    const privacyLink = document.getElementById('privacy-link');
    const privacyClose = document.getElementById('privacy-close');
    const declinePrivacy = document.getElementById('decline-privacy');
    const acceptPrivacy = document.getElementById('accept-privacy');

    // Terms Modal Events
    if (termsLink) {
        termsLink.addEventListener('click', (e) => {
            e.preventDefault();
            termsModal.style.display = 'block';
        });
    }

    if (termsClose) {
        termsClose.addEventListener('click', () => {
            termsModal.style.display = 'none';
        });
    }

    if (declineTerms) {
        declineTerms.addEventListener('click', () => {
            termsModal.style.display = 'none';
            // Uncheck the terms checkbox
            const termsCheckbox = document.getElementById('terms');
            if (termsCheckbox) {
                termsCheckbox.checked = false;
            }
        });
    }

    if (acceptTerms) {
        acceptTerms.addEventListener('click', () => {
            termsModal.style.display = 'none';
            // Check the terms checkbox
            const termsCheckbox = document.getElementById('terms');
            if (termsCheckbox) {
                termsCheckbox.checked = true;
            }
        });
    }

    // Privacy Modal Events
    if (privacyLink) {
        privacyLink.addEventListener('click', (e) => {
            e.preventDefault();
            privacyModal.style.display = 'block';
        });
    }

    if (privacyClose) {
        privacyClose.addEventListener('click', () => {
            privacyModal.style.display = 'none';
        });
    }

    if (declinePrivacy) {
        declinePrivacy.addEventListener('click', () => {
            privacyModal.style.display = 'none';
        });
    }

    if (acceptPrivacy) {
        acceptPrivacy.addEventListener('click', () => {
            privacyModal.style.display = 'none';
        });
    }

    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === termsModal) {
            termsModal.style.display = 'none';
        }
        if (e.target === privacyModal) {
            privacyModal.style.display = 'none';
        }
    });
};

navSlide();
carousel();
handleMobileButtons();
loadMainPageData();
handleMultiStepForm();
handleForms();
handleModal();
