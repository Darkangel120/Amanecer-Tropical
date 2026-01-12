const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function () {
    console.log('Reservations page loaded');

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

    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    if (burger && nav) {
        burger.addEventListener('click', () => {
            nav.classList.toggle('nav-active');
            burger.classList.toggle('toggle');
        });

        navLinks.forEach((link, index) => {
            link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
        });
    }

    loadUserReservations();

    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function (e) {
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
        let serviceImage = '';
        let keyDetails = [];

        switch (reservation.tipoServicio) {
            case 'paquete':
                if (reservation.paquete) {
                    serviceName = reservation.paquete.nombre;
                    serviceLocation = reservation.paquete.ubicacion;
                    serviceImage = reservation.paquete.urlImagen;
                    keyDetails = [
                        `Categoría: ${reservation.paquete.categoria}`,
                        `Duración: ${reservation.paquete.duracionDias} días`,
                        `Precio: $${parseFloat(reservation.precioTotal).toFixed(2)}`
                    ];
                }
                break;
            case 'vuelo':
                if (reservation.vuelo) {
                    serviceName = `${reservation.vuelo.aerolinea} - Vuelo ${reservation.vuelo.flightNumber}`;
                    serviceLocation = `${reservation.vuelo.origen} → ${reservation.vuelo.destino}`;
                    serviceImage = '../assets/img/flight-placeholder.jpg';
                    keyDetails = [
                        `Clase: ${reservation.vuelo.classType}`,
                        `Salida: ${new Date(reservation.vuelo.departureTime).toLocaleDateString('es-ES')}`,
                        `Precio: $${parseFloat(reservation.precioTotal).toFixed(2)}`
                    ];
                }
                break;
            case 'hotel':
                if (reservation.hotel) {
                    serviceName = reservation.hotel.nombre;
                    serviceLocation = reservation.hotel.ubicacion;
                    serviceImage = reservation.hotel.urlImagen;
                    keyDetails = [
                        `Estrellas: ${'★'.repeat(reservation.hotel.estrellas)}`,
                        `Precio por noche: $${parseFloat(reservation.hotel.precioPorNoche).toFixed(2)}`,
                        `Precio total: $${parseFloat(reservation.precioTotal).toFixed(2)}`
                    ];
                }
                break;
            case 'vehiculo':
                if (reservation.vehiculo) {
                    serviceName = reservation.vehiculo.nombre;
                    serviceLocation = reservation.vehiculo.ubicacion;
                    serviceImage = reservation.vehiculo.urlImagen;
                    keyDetails = [
                        `Capacidad: ${reservation.vehiculo.capacidad} personas`,
                        `Transmisión: ${reservation.vehiculo.transmision}`,
                        `Precio por día: $${parseFloat(reservation.vehiculo.precioPorDia).toFixed(2)}`
                    ];
                }
                break;
            case 'personalizado':
                serviceName = 'Reserva Personalizada';
                serviceLocation = reservation.hotel ? reservation.hotel.ubicacion : 'Ubicación personalizada';
                serviceImage = '';
                let servicesIncluded = [];
                if (reservation.vuelo) servicesIncluded.push('Vuelo');
                if (reservation.hotel) servicesIncluded.push('Hotel');
                if (reservation.vehiculo) servicesIncluded.push('Vehículo');
                keyDetails = [
                    `Servicios incluidos: ${servicesIncluded.join(', ') || 'Ninguno'}`,
                    `Precio total: $${parseFloat(reservation.precioTotal).toFixed(2)}`
                ];
                break;
            default:
                serviceName = 'Servicio general';
                keyDetails = [`Tipo: ${reservation.tipoServicio || 'Servicio'}`];
        }

        const reservationCard = document.createElement('div');
        reservationCard.className = 'reservation-card';

        const canModify = reservation.estado === 'pendiente' || reservation.estado === 'confirmado';

        reservationCard.innerHTML = `
    <div class="reservation-header">
        <div class="service-info">
            <h3>${serviceName}</h3>
            <p class="service-location">
                <i class="fas fa-map-marker-alt"></i> ${serviceLocation}
            </p>
        </div>
    </div>
    <div class="reservation-preview">
        <div class="preview-details">
            ${keyDetails.map(detail => `<p class="detail-item">${detail}</p>`).join('')}
        </div>
        <div class="reservation-summary">
            <div class="summary-item">
                <i class="fas fa-calendar-alt"></i>
                <span>
                    ${new Date(reservation.fechaInicio).toLocaleDateString('es-ES')} -
                    ${new Date(reservation.fechaFin).toLocaleDateString('es-ES')}
                </span>
            </div>
            <div class="summary-item">
                <i class="fas fa-users"></i>
                <span>
                    ${reservation.numeroPersonas} persona${reservation.numeroPersonas > 1 ? 's' : ''}
                </span>
            </div>
            <div class="summary-item">
                <i class="fas fa-info-circle"></i>
                <span class="status-${reservation.estado}">${getStatusText(reservation.estado)}</span>
            </div>
        </div>
    </div>
    <div class="reservation-actions">
        <button class="btn-details" data-id="${reservation.id}">
            <i class="fas fa-qrcode"></i> Ver detalles
        </button>
        <button class="btn-modify" data-id="${reservation.id}" ${!canModify ? 'disabled' : ''}>
            <i class="fas fa-edit"></i> Modificar
        </button>
        <button class="btn-cancel" data-id="${reservation.id}" ${!canModify ? 'disabled' : ''}>
            <i class="fas fa-times"></i> Cancelar
        </button>
    </div>
`;
        reservationsContainer.appendChild(reservationCard);
    });

    document.querySelectorAll('.btn-details').forEach(btn => {
        btn.addEventListener('click', function () {
            const reservationId = this.getAttribute('data-id');
            showQRCode(reservationId);
        });
    });



    document.querySelectorAll('.btn-modify').forEach(btn => {
        btn.addEventListener('click', function () {
            if (!this.disabled) {
                const reservationId = this.getAttribute('data-id');
                modifyReservation(reservationId);
            }
        });
    });

    document.querySelectorAll('.btn-cancel').forEach(btn => {
        btn.addEventListener('click', function () {
            if (!this.disabled) {
                const reservationId = this.getAttribute('data-id');
                cancelReservation(reservationId);
            }
        });
    });
}


async function modifyReservation(reservationId) {
    try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`${API_BASE_URL}/reservations/${reservationId}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (!response.ok) {
            alert('Error al cargar los datos de la reserva');
            return;
        }

        const reservation = await response.json();
        showModifyReservationModal(reservation);

    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}

function showModifyReservationModal(reservation) {
    const modalOverlay = document.createElement('div');
    modalOverlay.className = 'modal-overlay';
    modalOverlay.innerHTML = `
        <div class="reservation-modal modify-modal">
            <div class="modal-header">
                <h2>Modificar Reserva #${reservation.id}</h2>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                <form id="modify-reservation-form">
                    <div class="form-group">
                        <label for="modify-fecha-inicio">
                            <i class="fas fa-calendar-alt"></i> Fecha de Inicio:
                        </label>
                        <input type="date" id="modify-fecha-inicio" name="fechaInicio" 
                               value="${reservation.fechaInicio}" 
                               min="${new Date().toISOString().split('T')[0]}" 
                               required>
                    </div>

                    <div class="form-group">
                        <label for="modify-fecha-fin">
                            <i class="fas fa-calendar-check"></i> Fecha de Fin:
                        </label>
                        <input type="date" id="modify-fecha-fin" name="fechaFin" 
                               value="${reservation.fechaFin}" 
                               min="${new Date().toISOString().split('T')[0]}" 
                               required>
                    </div>

                    <div class="form-group">
                        <label for="modify-num-personas">
                            <i class="fas fa-users"></i> Número de Personas:
                        </label>
                        <input type="number" id="modify-num-personas" name="numeroPersonas" 
                               value="${reservation.numeroPersonas}" 
                               min="1" max="20" required>
                    </div>

                    <div class="form-group">
                        <label for="modify-solicitudes">
                            <i class="fas fa-sticky-note"></i> Solicitudes Especiales:
                        </label>
                        <textarea id="modify-solicitudes" name="solicitudesEspeciales" 
                                  rows="4">${reservation.solicitudesEspeciales || ''}</textarea>
                    </div>

                    <div class="price-info">
                        <p><strong>Precio Total:</strong> $${parseFloat(reservation.precioTotal).toFixed(2)}</p>
                        <p class="note">El precio se recalculará automáticamente según las nuevas fechas.</p>
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="btn-secondary" onclick="this.closest('.modal-overlay').remove()">
                            Cancelar
                        </button>
                        <button type="submit" class="btn-primary">
                            <i class="fas fa-save"></i> Guardar Cambios
                        </button>
                    </div>
                </form>
            </div>
        </div>
    `;

    document.body.appendChild(modalOverlay);

    modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
        modalOverlay.remove();
    });

    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.remove();
        }
    });

    const form = modalOverlay.querySelector('#modify-reservation-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await submitModifiedReservation(reservation, form);
    });

    const fechaInicio = modalOverlay.querySelector('#modify-fecha-inicio');
    const fechaFin = modalOverlay.querySelector('#modify-fecha-fin');

    fechaInicio.addEventListener('change', () => {
        fechaFin.min = fechaInicio.value;
        if (fechaFin.value && fechaFin.value <= fechaInicio.value) {
            fechaFin.value = '';
        }
    });
}

async function submitModifiedReservation(originalReservation, form) {
    const formData = new FormData(form);

    const fechaInicio = formData.get('fechaInicio');
    const fechaFin = formData.get('fechaFin');

    if (new Date(fechaFin) <= new Date(fechaInicio)) {
        alert('La fecha de fin debe ser posterior a la fecha de inicio');
        return;
    }

    const updatedReservation = {
        id: originalReservation.id,
        usuario: originalReservation.usuario,
        paquete: originalReservation.paquete,
        vuelo: originalReservation.vuelo,
        hotel: originalReservation.hotel,
        vehiculo: originalReservation.vehiculo,
        tipoServicio: originalReservation.tipoServicio,
        fechaInicio: fechaInicio,
        fechaFin: fechaFin,
        numeroPersonas: parseInt(formData.get('numeroPersonas')),
        precioTotal: originalReservation.precioTotal,
        estado: originalReservation.estado,
        solicitudesEspeciales: formData.get('solicitudesEspeciales')
    };

    try {
        const response = await fetch(`${API_BASE_URL}/reservations/${originalReservation.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(updatedReservation)
        });

        if (response.ok) {
            alert('Reserva modificada exitosamente');
            document.querySelector('.modal-overlay').remove();
            loadUserReservations();
        } else {
            const errorText = await response.text();
            alert('Error al modificar la reserva: ' + errorText);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}


async function showQRCode(reservationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/reservations/${reservationId}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (!response.ok) {
            alert('Error al cargar los datos de la reserva');
            return;
        }

        const reservation = await response.json();
        createQRModal(reservation);

    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}

function createQRModal(reservation) {
    const qrData = generateQRData(reservation);

    const modalOverlay = document.createElement('div');
    modalOverlay.className = 'modal-overlay';
    modalOverlay.innerHTML = `
        <div class="reservation-modal qr-modal">
            <div class="modal-header">
                <h2>Código QR de Reserva</h2>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                <div class="qr-container">
                    <div id="qr-code-display"></div>
                    <p class="qr-instructions">
                        Escanea este código QR para acceder a los detalles de tu reserva
                    </p>
                </div>

                <div class="qr-actions">
                    <button class="btn-primary" onclick="downloadQR(${reservation.id})">
                        <i class="fas fa-download"></i> Descargar QR
                    </button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modalOverlay);

    generateQRImage(qrData, 'qr-code-display');

    modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
        modalOverlay.remove();
    });

    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.remove();
        }
    });
}

function generateQRData(reservation) {
    const qrText = `
        Confirme su reserva aquí:
        ${window.location.origin}/frontend/web/user/verify-reservation.html?id=${reservation.id}
`.trim();

    return qrText;
}

function generateQRImage(data, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(data)}`;

    container.innerHTML = `
        <img src="${qrUrl}" alt="QR Code" class="qr-image" />
    `;
}

function downloadQR(reservationId) {
    const qrImage = document.querySelector('.qr-image');
    if (!qrImage) return;

    const link = document.createElement('a');
    link.href = qrImage.src;
    link.download = `reserva-${reservationId}-qr.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
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
            loadUserReservations();
        } else {
            const error = await response.text();
            alert('Error al cancelar la reserva: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}


function showReservationDetails(reservationId) {
    const user = JSON.parse(localStorage.getItem('user'));
    fetch(`${API_BASE_URL}/reservations/${reservationId}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
        .then(response => response.json())
        .then(reservation => {
            createReservationModal(reservation);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al cargar los detalles de la reserva');
        });
}

function createReservationModal(reservation) {
    const modalOverlay = document.createElement('div');
    modalOverlay.className = 'modal-overlay';
    modalOverlay.innerHTML = `
        <div class="reservation-modal">
            <div class="modal-header">
                <h2>Detalles de la Reserva #${reservation.id}</h2>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                ${generateReservationDetailsHTML(reservation)}
                <div class="modal-actions">
                    <button class="btn-qr" data-id="${reservation.id}">
                        <i class="fas fa-qrcode"></i> Ver QR
                    </button>
                    <button class="btn-modify" data-id="${reservation.id}" 
                            ${(reservation.estado === 'cancelado' || reservation.estado === 'completado') ? 'disabled' : ''}>
                        <i class="fas fa-edit"></i> Modificar
                    </button>
                    <button class="btn-cancel" data-id="${reservation.id}"
                            ${(reservation.estado === 'cancelado' || reservation.estado === 'completado') ? 'disabled' : ''}>
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modalOverlay);

    modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
        modalOverlay.remove();
    });

    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.remove();
        }
    });

    modalOverlay.querySelector('.btn-qr').addEventListener('click', function () {
        modalOverlay.remove();
        showQRCode(this.getAttribute('data-id'));
    });

    modalOverlay.querySelector('.btn-modify').addEventListener('click', function () {
        if (!this.disabled) {
            modalOverlay.remove();
            modifyReservation(this.getAttribute('data-id'));
        }
    });

    modalOverlay.querySelector('.btn-cancel').addEventListener('click', function () {
        if (!this.disabled) {
            modalOverlay.remove();
            cancelReservation(this.getAttribute('data-id'));
        }
    });
}

function generateReservationDetailsHTML(reservation) {
    let serviceDetails = '';
    let serviceImage = '';

    switch (reservation.tipoServicio) {
        case 'paquete':
            if (reservation.paquete) {
                serviceImage = reservation.paquete.urlImagen;
                serviceDetails = `
                    <div class="detail-section">
                        <h3><i class="fas fa-suitcase"></i> Información del Paquete</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <strong>Nombre:</strong> ${reservation.paquete.nombre}
                            </div>
                            <div class="detail-item">
                                <strong>Categoría:</strong> ${reservation.paquete.categoria}
                            </div>
                            <div class="detail-item">
                                <strong>Ubicación:</strong> ${reservation.paquete.ubicacion}
                            </div>
                            <div class="detail-item">
                                <strong>Duración:</strong> ${reservation.paquete.duracionDias} días
                            </div>
                            <div class="detail-item full-width">
                                <strong>Incluye:</strong> ${reservation.paquete.incluye.replace(/[\[\]"]/g, '')}
                            </div>
                            <div class="detail-item full-width">
                                <strong>Descripción:</strong> ${reservation.paquete.descripcion}
                            </div>
                            <div class="detail-item full-width">
                                <strong>Itinerario:</strong> ${reservation.paquete.itinerario.replace(/[\[\]"]/g, '')}
                            </div>
                        </div>
                    </div>
                `;
            }
            break;
        case 'vuelo':
            if (reservation.vuelo) {
                serviceImage = '../assets/img/flight-placeholder.jpg';
                serviceDetails = `
                    <div class="detail-section">
                        <h3><i class="fas fa-plane"></i> Información del Vuelo</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <strong>Número de vuelo:</strong> ${reservation.vuelo.flightNumber}
                            </div>
                            <div class="detail-item">
                                <strong>Aerolínea:</strong> ${reservation.vuelo.aerolinea}
                            </div>
                            <div class="detail-item">
                                <strong>Origen:</strong> ${reservation.vuelo.origen}
                            </div>
                            <div class="detail-item">
                                <strong>Destino:</strong> ${reservation.vuelo.destino}
                            </div>
                            <div class="detail-item">
                                <strong>Hora de salida:</strong> ${new Date(reservation.vuelo.departureTime).toLocaleString('es-ES')}
                            </div>
                            <div class="detail-item">
                                <strong>Hora de llegada:</strong> ${new Date(reservation.vuelo.arrivalTime).toLocaleString('es-ES')}
                            </div>
                            <div class="detail-item">
                                <strong>Tipo de clase:</strong> ${reservation.vuelo.classType}
                            </div>
                            <div class="detail-item">
                                <strong>Tipo de avión:</strong> ${reservation.vuelo.aircraftType}
                            </div>
                        </div>
                    </div>
                `;
            }
            break;
        case 'hotel':
            if (reservation.hotel) {
                serviceImage = reservation.hotel.urlImagen;
                serviceDetails = `
                    <div class="detail-section">
                        <h3><i class="fas fa-hotel"></i> Información del Hotel</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <strong>Nombre:</strong> ${reservation.hotel.nombre}
                            </div>
                            <div class="detail-item">
                                <strong>Ubicación:</strong> ${reservation.hotel.ubicacion}
                            </div>
                            <div class="detail-item">
                                <strong>Estrellas:</strong> ${'★'.repeat(reservation.hotel.estrellas)}
                            </div>
                            <div class="detail-item">
                                <strong>Precio por noche:</strong> $${parseFloat(reservation.hotel.precioPorNoche).toFixed(2)}
                            </div>
                            <div class="detail-item full-width">
                                <strong>Comodidades:</strong> ${reservation.hotel.comodidades}
                            </div>
                            <div class="detail-item full-width">
                                <strong>Descripción:</strong> ${reservation.hotel.descripcion}
                            </div>
                        </div>
                    </div>
                `;
            }
            break;
        case 'vehiculo':
            if (reservation.vehiculo) {
                serviceImage = reservation.vehiculo.urlImagen;
                serviceDetails = `
                    <div class="detail-section">
                        <h3><i class="fas fa-car"></i> Información del Vehículo</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <strong>Nombre:</strong> ${reservation.vehiculo.nombre}
                            </div>
                            <div class="detail-item">
                                <strong>Tipo:</strong> ${reservation.vehiculo.tipo}
                            </div>
                            <div class="detail-item">
                                <strong>Capacidad:</strong> ${reservation.vehiculo.capacidad} personas
                            </div>
                            <div class="detail-item">
                                <strong>Transmisión:</strong> ${reservation.vehiculo.transmision}
                            </div>
                            <div class="detail-item">
                                <strong>Combustible:</strong> ${reservation.vehiculo.tipoCombustible}
                            </div>
                            <div class="detail-item">
                                <strong>Precio por día:</strong> $${parseFloat(reservation.vehiculo.precioPorDia).toFixed(2)}
                            </div>
                            <div class="detail-item full-width">
                                <strong>Descripción:</strong> ${reservation.vehiculo.descripcion}
                            </div>
                        </div>
                    </div>
                `;
            }
            break;
    }
    return `
    <div class="reservation-details-modal">
        <div class="service-header">
            ${serviceImage ? `<img src="${serviceImage}" alt="Service Image" class="service-modal-image">` : ''}
            <div class="service-info">
                <h3>${reservation.tipoServicio.charAt(0).toUpperCase() + reservation.tipoServicio.slice(1)} Reservado</h3>
                <button class="btn-qr" data-id="${reservation.id}">
                    <i class="fas fa-qrcode"></i>
                </button>
            </div>
        </div>

        ${serviceDetails}

        <div class="detail-section">
            <h3><i class="fas fa-calendar-alt"></i> Fechas de la Reserva</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <strong>Fecha de inicio:</strong> ${new Date(reservation.fechaInicio).toLocaleDateString('es-ES')}
                </div>
                <div class="detail-item">
                    <strong>Fecha de fin:</strong> ${new Date(reservation.fechaFin).toLocaleDateString('es-ES')}
                </div>
                <div class="detail-item">
                    <strong>Duración:</strong> ${Math.ceil((new Date(reservation.fechaFin) - new Date(reservation.fechaInicio)) / (1000 * 60 * 60 * 24))} días
                </div>
            </div>
        </div>

        <div class="detail-section">
            <h3><i class="fas fa-users"></i> Información de Reserva</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <strong>Número de personas:</strong> ${reservation.numeroPersonas}
                </div>
                <div class="detail-item">
                    <strong>Precio total:</strong> $${parseFloat(reservation.precioTotal).toFixed(2)}
                </div>
                <div class="detail-item">
                    <strong>Estado:</strong> <span class="status-${reservation.estado}">${getStatusText(reservation.estado)}</span>
                </div>
            </div>
        </div>

        ${reservation.solicitudesEspeciales ? `
        <div class="detail-section">
            <h3><i class="fas fa-sticky-note"></i> Solicitudes Especiales</h3>
            <p>${reservation.solicitudesEspeciales}</p>
        </div>
        ` : ''}
    </div>
`;
}

function getStatusText(status) {
    const statusMap = {
        'pendiente': 'Pendiente',
        'confirmado': 'Confirmado',
        'cancelado': 'Cancelado',
        'completado': 'Completado'
    };
    return statusMap[status] || status;
}
