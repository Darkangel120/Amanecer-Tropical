// Dashboard JavaScript
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

    // Logout functionality
    const logoutBtn = document.querySelector('.logout-option');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../index.html';
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

    // Load user info
    loadUserInfo();

    // Load user stats
    loadUserStats();
});


async function loadUserInfo() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user) {
        const userNameElement = document.querySelector('.user-name');
        if (userNameElement) {
            userNameElement.textContent = user.nombre;
        }
    }
}

async function loadUserStats() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) {
        console.log('No user found in localStorage');
        return;
    }

    console.log('Loading stats for user:', user.id);

    // Show loading indicators
    const statCards = document.querySelectorAll('.stat-card');
    console.log('Found stat cards:', statCards.length);
    statCards.forEach(card => {
        const h3 = card.querySelector('h3');
        const p = card.querySelector('p');
        if (h3) h3.textContent = '...';
        if (p) p.textContent = 'Cargando...';
    });

    try {
        const token = localStorage.getItem('token');
        console.log('Token available:', !!token);

        // Fetch reservations and notifications in parallel
        const [reservationsResponse, notificationsResponse] = await Promise.all([
            fetch(`${API_BASE_URL}/reservations/user/${user.id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            }),
            fetch(`${API_BASE_URL}/notifications/user/${user.id}/unread`, {
                headers: { 'Authorization': `Bearer ${token}` }
            })
        ]);

        console.log('Reservations response status:', reservationsResponse.status);
        console.log('Notifications response status:', notificationsResponse.status);

        if (!reservationsResponse.ok) {
            throw new Error(`Error al cargar reservas: ${reservationsResponse.status}`);
        }

        const reservations = await reservationsResponse.json();
        console.log('Reservations loaded:', reservations.length);
        let unreadNotifications = 0;

        if (notificationsResponse.ok) {
            const notifications = await notificationsResponse.json();
            unreadNotifications = notifications.length;
            console.log('Unread notifications:', unreadNotifications);
        } else {
            console.warn('No se pudieron cargar las notificaciones no leídas');
        }

        // Count active reservations (pendiente or confirmado)
        const activeReservations = reservations.filter(r => r.estado === 'pendiente' || r.estado === 'confirmado').length;

        // Count total reservations
        const totalReservations = reservations.length;

        // Count completed trips
        const completedTrips = reservations.filter(r => r.estado === 'completado').length;

        // Find next trip (earliest start date)
        const upcomingReservations = reservations.filter(r => new Date(r.fechaInicio) > new Date());
        let nextTrip = 'Ninguno';
        if (upcomingReservations.length > 0) {
            const nextReservation = upcomingReservations.reduce((earliest, current) =>
                new Date(current.fechaInicio) < new Date(earliest.fechaInicio) ? current : earliest
            );
            // Get service name from the reservation
            const serviceName = getServiceName(nextReservation);
            nextTrip = `${serviceName} - ${new Date(nextReservation.fechaInicio).toLocaleDateString('es-ES')}`;
        }

        // Calculate total spent (confirmed reservations)
        const totalSpent = reservations
            .filter(r => r.estado === 'confirmado')
            .reduce((sum, r) => sum + parseFloat(r.precioTotal), 0);

        // Update the HTML elements
        // Active reservations (first stat card)
        const activeReservationsElement = statCards[0].querySelector('h3');
        if (activeReservationsElement) {
            activeReservationsElement.textContent = activeReservations;
        }

        // Next trip (second stat card)
        const nextTripElement = statCards[1].querySelector('h3');
        if (nextTripElement) {
            nextTripElement.textContent = nextTrip;
        }

        // Total spent (third stat card)
        const totalSpentElement = statCards[2].querySelector('h3');
        if (totalSpentElement) {
            totalSpentElement.textContent = `$${totalSpent.toFixed(2)}`;
        }

        // Add notification indicator if there are unread notifications
        if (unreadNotifications > 0) {
            const navbar = document.querySelector('.navbar');
            if (navbar && !document.querySelector('.notification-badge')) {
                const badge = document.createElement('span');
                badge.className = 'notification-badge';
                badge.textContent = unreadNotifications;
                badge.style.cssText = `
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    background: #ff4444;
                    color: white;
                    border-radius: 50%;
                    padding: 2px 6px;
                    font-size: 12px;
                    font-weight: bold;
                `;
                navbar.style.position = 'relative';
                navbar.appendChild(badge);
            }
        }

    } catch (error) {
        console.error('Error al cargar estadísticas del usuario:', error);

        // Show user-friendly error messages
        const statCards = document.querySelectorAll('.stat-card');
        statCards.forEach(card => {
            const h3 = card.querySelector('h3');
            const p = card.querySelector('p');
            if (h3) h3.textContent = 'Error';
            if (p) p.textContent = 'No se pudo cargar';
        });

        // Show error notification
        showErrorMessage('No se pudieron cargar las estadísticas. Por favor, intenta recargar la página.');
    }
}

function showErrorMessage(message) {
    // Create and show error message
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    errorDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #ff4444;
        color: white;
        padding: 10px 20px;
        border-radius: 5px;
        z-index: 1000;
        font-family: 'Poppins', sans-serif;
    `;

    document.body.appendChild(errorDiv);

    // Remove after 5 seconds
    setTimeout(() => {
        if (errorDiv.parentNode) {
            errorDiv.parentNode.removeChild(errorDiv);
        }
    }, 5000);
}
