const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
    console.log('Reservations page loaded');

    // Check authentication
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    console.log('Token:', token ? 'present' : 'missing');
    console.log('User:', user);

    if (!token) {
        console.log('No token found, redirecting to login');
        window.location.href = 'login.html';
        return;
    }

    if (!user) {
        console.log('No user found in localStorage, redirecting to login');
        window.location.href = 'login.html';
        return;
    }

    // Navbar toggle for mobile
    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    if (burger && nav) {
        burger.addEventListener('click', () => {
            nav.classList.toggle('nav-active');
            burger.classList.toggle('toggle');
        });

        // Animate nav links
        navLinks.forEach((link, index) => {
            link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
        });
    }

    // Load user reservations
    loadUserReservations();

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

async function loadUserReservations() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/reservations/user/${user.id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
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
    console.log('Displaying reservations:', reservations);

    const reservationsContainer = document.querySelector('.reservations-grid');
    if (!reservationsContainer) {
        console.error('Reservations container not found');
        return;
    }

    reservationsContainer.innerHTML = '';

    if (reservations.length === 0) {
        console.log('No reservations found');
        reservationsContainer.innerHTML = '<p class="no-reservations">No tienes reservas activas.</p>';
        return;
    }

    reservations.forEach(reservation => {
        let serviceName = 'Servicio desconocido';
        let serviceLocation = 'Ubicación desconocida';
        let serviceDetails = 'Detalles no disponibles';

        // Determine service type and get appropriate details
        switch (reservation.tipoServicio) {
            case 'paquete':
                serviceName = reservation.paquete ? reservation.paquete.nombre : 'Paquete desconocido';
                serviceLocation = reservation.paquete ? reservation.paquete.ubicacion : 'Ubicación desconocida';
                serviceDetails = `Paquete turístico - ${reservation.paquete ? reservation.paquete.categoria : 'Categoría desconocida'}`;
                break;
            case 'vuelo':
                serviceName = reservation.vuelo ? `${reservation.vuelo.aerolinea} - ${reservation.vuelo.flightNumber}` : 'Vuelo desconocido';
                serviceLocation = reservation.vuelo ? `${reservation.vuelo.origen} → ${reservation.vuelo.destino}` : 'Ruta desconocida';
                serviceDetails = `Vuelo - ${reservation.vuelo ? reservation.vuelo.tipoClase : 'Clase desconocida'}`;
                break;
            case 'hotel':
                serviceName = reservation.hotel ? reservation.hotel.nombre : 'Hotel desconocido';
                serviceLocation = reservation.hotel ? reservation.hotel.ubicacion : 'Ubicación desconocida';
                serviceDetails = `Hotel - ${reservation.hotel ? reservation.hotel.estrellas + ' estrellas' : 'Calificación desconocida'}`;
                break;
            case 'vehiculo':
                serviceName = reservation.vehiculo ? reservation.vehiculo.nombre : 'Vehículo desconocido';
                serviceLocation = reservation.vehiculo ? `${reservation.vehiculo.tipo} - ${reservation.vehiculo.transmision}` : 'Tipo desconocido';
                serviceDetails = `Vehículo - ${reservation.vehiculo ? reservation.vehiculo.capacidad + ' personas' : 'Capacidad desconocida'}`;
                break;
            default:
                serviceName = 'Servicio general';
                serviceDetails = reservation.tipoServicio || 'Servicio';
        }

        const reservationCard = document.createElement('div');
        reservationCard.className = 'reservation-card';
        reservationCard.innerHTML = `
            <h3>${serviceName}</h3>
            <p><strong>Tipo de servicio:</strong> ${reservation.tipoServicio}</p>
            <p><strong>Ubicación/Detalles:</strong> ${serviceLocation}</p>
            <p><strong>Detalles adicionales:</strong> ${serviceDetails}</p>
            <p><strong>Fecha de inicio:</strong> ${new Date(reservation.fechaInicio).toLocaleDateString()}</p>
            <p><strong>Fecha de fin:</strong> ${new Date(reservation.fechaFin).toLocaleDateString()}</p>
            <p><strong>Número de personas:</strong> ${reservation.numeroPersonas}</p>
            <p><strong>Precio total:</strong> $${reservation.precioTotal.toFixed(2)}</p>
            <p><strong>Estado:</strong> <span class="status-${reservation.estado}">${reservation.estado}</span></p>
            ${reservation.solicitudesEspeciales ? `<p><strong>Solicitudes especiales:</strong> ${reservation.solicitudesEspeciales}</p>` : ''}
            <p><strong>Fecha de creación:</strong> ${reservation.fechaCreacion ? new Date(reservation.fechaCreacion).toLocaleString() : 'N/A'}</p>
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
    
    // In a real implementation, you would:
    // 1. Fetch the reservation details
    // 2. Show a modal with the current data
    // 3. Allow the user to modify dates, number of people, etc.
    // 4. Send a PUT request to /api/reservations/{id}
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