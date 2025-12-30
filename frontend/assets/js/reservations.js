// Reservations JavaScript
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

    // Modify reservation buttons
    const modifyButtons = document.querySelectorAll('.btn-modify');
    modifyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reservationCard = this.closest('.reservation-card');
            const reservationTitle = reservationCard.querySelector('h3').textContent;
            alert(`Funcionalidad para modificar "${reservationTitle}" próximamente disponible.`);
        });
    });

    // Cancel reservation buttons
    const cancelButtons = document.querySelectorAll('.btn-cancel');
    cancelButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reservationCard = this.closest('.reservation-card');
            const reservationTitle = reservationCard.querySelector('h3').textContent;
            
            const confirmCancel = confirm(`¿Estás seguro de que quieres cancelar "${reservationTitle}"? Esta acción no se puede deshacer.`);
            if (confirmCancel) {
                reservationCard.style.opacity = '0.5';
                reservationCard.style.pointerEvents = 'none';
                alert(`"${reservationTitle}" ha sido cancelada.`);
                // Here you would typically send a request to the server to cancel the reservation
            }
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
