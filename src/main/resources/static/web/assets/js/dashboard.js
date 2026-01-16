const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    burger.addEventListener('click', () => {
        nav.classList.toggle('nav-active');
        burger.classList.toggle('toggle');
    });

    navLinks.forEach((link, index) => {
        link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
    });

    const logoutBtn = document.querySelector('.logout-option');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../index.html';
        });
    }

    const sectionCards = document.querySelectorAll('.section-card');
    sectionCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    const sidebarToggle = document.getElementById('sidebarToggle');
    const activitySidebar = document.querySelector('.activity-sidebar');

    if (sidebarToggle && activitySidebar) {
        sidebarToggle.addEventListener('click', function() {
            activitySidebar.classList.toggle('collapsed');
        });
    }

    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 768) {
            if (!activitySidebar.contains(event.target) && !sidebarToggle.contains(event.target)) {
                activitySidebar.classList.add('collapsed');
            }
        }
    });

    loadUserInfo();

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

        const activeReservations = reservations.filter(r => r.estado === 'pendiente' || r.estado === 'confirmado').length;

        const totalReservations = reservations.length;

        const completedTrips = reservations.filter(r => r.estado === 'completado').length;

        const upcomingReservations = reservations.filter(r => new Date(r.fechaInicio) > new Date());
        let nextTrip = 'Ninguno';
        if (upcomingReservations.length > 0) {
            const nextReservation = upcomingReservations.reduce((earliest, current) =>
                new Date(current.fechaInicio) < new Date(earliest.fechaInicio) ? current : earliest
            );
            const serviceName = getServiceName(nextReservation);
            nextTrip = `${serviceName} - ${new Date(nextReservation.fechaInicio + 'T00:00:00').toLocaleDateString('es-ES')}`;
        }

        const totalSpent = reservations
            .filter(r => r.estado === 'confirmado')
            .reduce((sum, r) => sum + parseFloat(r.precioTotal), 0);

        const activeReservationsElement = statCards[0].querySelector('h3');
        if (activeReservationsElement) {
            activeReservationsElement.textContent = activeReservations;
        }

        const nextTripElement = statCards[1].querySelector('h3');
        if (nextTripElement) {
            nextTripElement.textContent = nextTrip;
        }

        const totalSpentElement = statCards[2].querySelector('h3');
        if (totalSpentElement) {
            totalSpentElement.textContent = `$${totalSpent.toFixed(2)}`;
        }

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

        const statCards = document.querySelectorAll('.stat-card');
        statCards.forEach(card => {
            const h3 = card.querySelector('h3');
            const p = card.querySelector('p');
            if (h3) h3.textContent = 'Error';
            if (p) p.textContent = 'No se pudo cargar';
        });

        showToast('No se pudieron cargar las estadísticas. Por favor, intenta recargar la página.', 'error');
    }
}

