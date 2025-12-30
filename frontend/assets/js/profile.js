// Profile JavaScript
document.addEventListener('DOMContentLoaded', function() {
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

    // Profile form submission
    const profileForm = document.querySelector('.profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Here you would typically send the form data to the server
            alert('Cambios guardados exitosamente.');
            
            // Reset form or update UI as needed
        });
    }

    // Logout confirmation
    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            const confirmLogout = confirm('¿Estás seguro de que quieres cerrar sesión?');
            if (!confirmLogout) {
                e.preventDefault();
            }
        });
    }
});
