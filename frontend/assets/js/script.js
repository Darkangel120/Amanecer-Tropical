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

const handleForms = () => {
    const loginForm = document.querySelector('#login-form');
    const registerForm = document.querySelector('#register-form');

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
                    alert('Inicio de sesión exitoso');
                    window.location.href = 'dashboard.html';
                } else {
                    const error = await response.text();
                    alert('Error en el inicio de sesión: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error de conexión. Asegúrate de que el backend esté ejecutándose.');
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            const userData = {
                name: formData.get('name'),
                email: formData.get('email'),
                password: formData.get('password'),
                phone: formData.get('phone'),
                role: 'USER'
            };

            try {
                const response = await fetch(`${API_BASE_URL}/users`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(userData),
                });

                if (response.ok) {
                    alert('Registro exitoso. Ahora puedes iniciar sesión.');
                    window.location.href = 'login.html';
                } else {
                    const error = await response.text();
                    alert('Error en el registro: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error de conexión. Asegúrate de que el backend esté ejecutándose.');
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
            alert('Por favor, complete todos los campos requeridos antes de continuar.');
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
handleMultiStepForm();
handleForms();
handleModal();
