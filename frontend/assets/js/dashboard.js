// Dashboard JavaScript
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

    // Add hover effects to section cards
    const sectionCards = document.querySelectorAll('.section-card');
    sectionCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    // Sidebar toggle functionality
    const sidebarToggle = document.getElementById('sidebarToggle');
    const activitySidebar = document.querySelector('.activity-sidebar');

    if (sidebarToggle && activitySidebar) {
        sidebarToggle.addEventListener('click', function() {
            activitySidebar.classList.toggle('collapsed');
        });
    }

    // Optional: Close sidebar when clicking outside (for mobile)
    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 768) {
            if (!activitySidebar.contains(event.target) && !sidebarToggle.contains(event.target)) {
                activitySidebar.classList.add('collapsed');
            }
        }
    });
});
