// Reservations JavaScript
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

    // Load user reservations
    loadUserReservations();

    // Logout functionality
    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'index.html';
        });
    }
});

async function loadUserReservations() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    try {
        const response = await fetch(`${API_BASE_URL}/reservations/user/${user.id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const reservations = await response.json();
            displayReservations(reservations);
        } else {
            console.error('Error loading reservations');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayReservations(reservations) {
    const reservationsContainer = document.querySelector('.reservations-container');
    if (!reservationsContainer) return;

    reservationsContainer.innerHTML = '';

    if (reservations.length === 0) {
        reservationsContainer.innerHTML = '<p>No tienes reservas activas.</p>';
        return;
    }

    reservations.forEach(reservation => {
        const reservationCard = document.createElement('div');
        reservationCard.className = 'reservation-card';
        reservationCard.innerHTML = `
            <h3>${reservation.destination ? reservation.destination.name : 'Destino desconocido'}</h3>
            <p><strong>Fecha de entrada:</strong> ${new Date(reservation.checkInDate).toLocaleDateString()}</p>
            <p><strong>Fecha de salida:</strong> ${new Date(reservation.checkOutDate).toLocaleDateString()}</p>
            <p><strong>Huéspedes:</strong> ${reservation.numberOfGuests}</p>
            <p><strong>Precio total:</strong> $${reservation.totalPrice}</p>
            <p><strong>Estado:</strong> ${reservation.status}</p>
            <div class="reservation-actions">
                <button class="btn-modify" data-id="${reservation.id}">Modificar</button>
                <button class="btn-cancel" data-id="${reservation.id}">Cancelar</button>
            </div>
        `;
        reservationsContainer.appendChild(reservationCard);
    });

    // Add event listeners for modify and cancel buttons
    document.querySelectorAll('.btn-modify').forEach(btn => {
        btn.addEventListener('click', function() {
            const reservationId = this.getAttribute('data-id');
            modifyReservation(reservationId);
        });
    });

    document.querySelectorAll('.btn-cancel').forEach(btn => {
        btn.addEventListener('click', function() {
            const reservationId = this.getAttribute('data-id');
            cancelReservation(reservationId);
        });
    });
}

async function modifyReservation(reservationId) {
    // For now, just show an alert. In a real app, you might open a modal or redirect to a modify page
    alert(`Funcionalidad para modificar reserva ${reservationId} próximamente disponible.`);
}

async function cancelReservation(reservationId) {
    const confirmCancel = confirm('¿Estás seguro de que quieres cancelar esta reserva? Esta acción no se puede deshacer.');
    if (!confirmCancel) return;

    try {
        const response = await fetch(`${API_BASE_URL}/reservations/${reservationId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            alert('Reserva cancelada exitosamente');
            loadUserReservations(); // Reload reservations
        } else {
            const error = await response.text();
            alert('Error al cancelar la reserva: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}
