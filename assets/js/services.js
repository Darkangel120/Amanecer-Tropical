// Services JavaScript
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

    // Tab functionality
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons and contents
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Add active class to clicked button and corresponding content
            this.classList.add('active');
            const tabId = this.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Booking buttons (placeholder functionality)
    const bookingButtons = document.querySelectorAll('.service-card .btn');
    bookingButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const serviceTitle = this.closest('.service-card').querySelector('h3').textContent;
            alert(`Funcionalidad de reserva para "${serviceTitle}" próximamente disponible.`);
        });
    });

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
