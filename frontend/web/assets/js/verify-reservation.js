const API_BASE_URL = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", async function () {
    // Validar sesión antes de proceder
    const isValidSession = await validateSession();
    if (!isValidSession) {
        return; // validateSession ya maneja la redirección
    }

    // Obtener ID de la reserva de la URL
    const urlParams = new URLSearchParams(window.location.search);
    const reservationId = urlParams.get("id");

    if (!reservationId) {
        showError("No se proporcionó un ID de reserva válido.");
        return;
    }

    await loadReservation(reservationId);
});

async function loadReservation(id) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/reservations/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                showError("La reserva no fue encontrada. Por favor verifica el ID.");
            } else if (response.status === 401) {
                showError("Sesión expirada. Por favor inicia sesión nuevamente.");
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                setTimeout(() => window.location.href = '../index.html', 2000);
            } else {
                showError("Error al cargar la información de la reserva.");
            }
            return;
        }

        const reservation = await response.json();
        displayReservation(reservation);
    } catch (error) {
        console.error("Error:", error);
        showError("Error de conexión. Por favor intenta nuevamente más tarde.");
    }
}

function displayReservation(reservation) {
    const content = document.getElementById("content");

    // Determinar información del servicio
    let serviceName = "Servicio";
    let serviceImage = "";
    let serviceDetails = "";

    switch (reservation.tipoServicio) {
        case "paquete":
            if (reservation.paquete) {
                serviceName = reservation.paquete.nombre;
                serviceImage = reservation.paquete.urlImagen;
                serviceDetails = `
                            <div class="info-item">
                                <label>Categoría</label>
                                <span>${reservation.paquete.categoria}</span>
                            </div>
                            <div class="info-item">
                                <label>Ubicación</label>
                                <span>${reservation.paquete.ubicacion}</span>
                            </div>
                            <div class="info-item">
                                <label>Duración</label>
                                <span>${reservation.paquete.duracionDias} días</span>
                            </div>
                        `;
            } else {
                serviceName = "Paquete Turístico";
                serviceDetails = `
                            <div class="info-item">
                                <label>Estado</label>
                                <span>Datos del paquete no disponibles</span>
                            </div>
                        `;
            }
            break;
        case "vuelo":
            if (reservation.vuelo) {
                serviceName = `${reservation.vuelo.aerolinea} - Vuelo ${reservation.vuelo.flightNumber}`;
                serviceImage = ""; // No image available for flights
                serviceDetails = `
                            <div class="info-item">
                                <label>Origen</label>
                                <span>${reservation.vuelo.origen}</span>
                            </div>
                            <div class="info-item">
                                <label>Destino</label>
                                <span>${reservation.vuelo.destino}</span>
                            </div>
                            <div class="info-item">
                                <label>Salida</label>
                                <span>${new Date(
                    reservation.vuelo.departureTime
                ).toLocaleString("es-ES")}</span>
                            </div>
                        `;
            } else {
                serviceName = "Vuelo";
                serviceDetails = `
                            <div class="info-item">
                                <label>Estado</label>
                                <span>Datos del vuelo no disponibles</span>
                            </div>
                        `;
            }
            break;
        case "hotel":
            if (reservation.hotel) {
                serviceName = reservation.hotel.nombre;
                serviceImage = reservation.hotel.urlImagen;
                serviceDetails = `
                            <div class="info-item">
                                <label>Ubicación</label>
                                <span>${reservation.hotel.ubicacion}</span>
                            </div>
                            <div class="info-item">
                                <label>Estrellas</label>
                                <span>${"★".repeat(
                    reservation.hotel.estrellas
                )}</span>
                            </div>
                            <div class="info-item">
                                <label>Precio por noche</label>
                                <span>$${parseFloat(
                    reservation.hotel.precioPorNoche
                ).toFixed(2)}</span>
                            </div>
                        `;
            } else {
                serviceName = "Hotel";
                serviceDetails = `
                            <div class="info-item">
                                <label>Estado</label>
                                <span>Datos del hotel no disponibles</span>
                            </div>
                        `;
            }
            break;
        case "vehiculo":
            if (reservation.vehiculo) {
                serviceName = reservation.vehiculo.nombre;
                serviceImage = reservation.vehiculo.urlImagen;
                serviceDetails = `
                            <div class="info-item">
                                <label>Tipo</label>
                                <span>${reservation.vehiculo.tipo}</span>
                            </div>
                            <div class="info-item">
                                <label>Capacidad</label>
                                <span>${reservation.vehiculo.capacidad} personas</span>
                            </div>
                            <div class="info-item">
                                <label>Transmisión</label>
                                <span>${reservation.vehiculo.transmision}</span>
                            </div>
                        `;
            } else {
                serviceName = "Vehículo";
                serviceDetails = `
                            <div class="info-item">
                                <label>Estado</label>
                                <span>Datos del vehículo no disponibles</span>
                            </div>
                        `;
            }
            break;
        case "personalizado":
            serviceName = "Reserva Personalizada";
            serviceImage = ""; // No single image for combined package
            let combinedDetails = "";

            if (reservation.vuelo) {
                combinedDetails += `
                    <div class="sub-service">
                        <h4><i class="fas fa-plane"></i> Vuelo: ${reservation.vuelo.aerolinea} - ${reservation.vuelo.flightNumber}</h4>
                        <div class="info-grid">
                            <div class="info-item">
                                <label>Origen</label>
                                <span>${reservation.vuelo.origen}</span>
                            </div>
                            <div class="info-item">
                                <label>Destino</label>
                                <span>${reservation.vuelo.destino}</span>
                            </div>
                            <div class="info-item">
                                <label>Salida</label>
                                <span>${new Date(reservation.vuelo.departureTime).toLocaleString("es-ES")}</span>
                            </div>
                        </div>
                    </div>
                `;
            }

            if (reservation.hotel) {
                combinedDetails += `
                    <div class="sub-service">
                        <h4><i class="fas fa-hotel"></i> Hotel: ${reservation.hotel.nombre}</h4>
                        <div class="info-grid">
                            <div class="info-item">
                                <label>Ubicación</label>
                                <span>${reservation.hotel.ubicacion}</span>
                            </div>
                            <div class="info-item">
                                <label>Estrellas</label>
                                <span>${"★".repeat(reservation.hotel.estrellas)}</span>
                            </div>
                            <div class="info-item">
                                <label>Precio por noche</label>
                                <span>$${parseFloat(reservation.hotel.precioPorNoche).toFixed(2)}</span>
                            </div>
                        </div>
                    </div>
                `;
            }

            if (reservation.vehiculo) {
                combinedDetails += `
                    <div class="sub-service">
                        <h4><i class="fas fa-car"></i> Vehículo: ${reservation.vehiculo.nombre}</h4>
                        <div class="info-grid">
                            <div class="info-item">
                                <label>Tipo</label>
                                <span>${reservation.vehiculo.tipo}</span>
                            </div>
                            <div class="info-item">
                                <label>Capacidad</label>
                                <span>${reservation.vehiculo.capacidad} personas</span>
                            </div>
                            <div class="info-item">
                                <label>Transmisión</label>
                                <span>${reservation.vehiculo.transmision}</span>
                            </div>
                        </div>
                    </div>
                `;
            }

            serviceDetails = combinedDetails || `
                <div class="info-item">
                    <label>Estado</label>
                    <span>No hay servicios disponibles en esta reserva personalizada</span>
                </div>
            `;
            break;

        default:
            serviceName = "Servicio Desconocido";
            serviceDetails = `
                        <div class="info-item">
                            <label>Tipo</label>
                            <span>${reservation.tipoServicio}</span>
                        </div>
                    `;
    }

    content.innerHTML = `
        <div class="reservation-details">
            <div style="text-align: center; margin-bottom: 20px;">
                <span class="status-badge status-${reservation.estado}">
                    ${getStatusIcon(reservation.estado)} ${getStatusText(reservation.estado)}
                </span>
            </div>

            ${serviceImage ? `<img src="../${serviceImage}" alt="${serviceName}" class="service-image">` : ""}

            <div class="info-section">
                <h3><i class="fas fa-${getServiceIcon(reservation.tipoServicio)}"></i> ${serviceName}</h3>
                <div class="info-grid">
                    ${serviceDetails}
                </div>
            </div>

            <div class="info-section">
                <h3><i class="fas fa-calendar-alt"></i> Información de la Reserva</h3>
                <div class="info-grid">
                    <div class="info-item">
                        <label>ID de Reserva</label>
                        <span>#${reservation.id}</span>
                    </div>
                    <div class="info-item">
                        <label>Fecha de Inicio</label>
                        <span>${new Date(reservation.fechaInicio).toLocaleDateString("es-ES", {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                        })}</span>
                    </div>
                    <div class="info-item">
                        <label>Fecha de Fin</label>
                        <span>${new Date(reservation.fechaFin).toLocaleDateString("es-ES", {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                        })}</span>
                    </div>
                    <div class="info-item">
                        <label>Número de Personas</label>
                        <span>${reservation.numeroPersonas}</span>
                    </div>
                    <div class="info-item">
                        <label>Precio Total</label>
                        <span style="font-size: 20px; color: #28a745; font-weight: bold;">$${parseFloat(reservation.precioTotal).toFixed(2)}</span>
                    </div>
                    <div class="info-item">
                        <label>Estado</label>
                        <span>${getStatusText(reservation.estado)}</span>
                    </div>
                </div>
            </div>

            ${reservation.pagos && reservation.pagos.length > 0 ? `
                <div class="info-section">
                    <h3><i class="fas fa-credit-card"></i> Información del Pago</h3>
                    <div class="info-grid">
                        ${reservation.pagos.map(pago => `
                            <div class="info-item">
                                <label>Estado del Pago</label>
                                <span>${getPaymentStatusText(pago.estadoPago)}</span>
                            </div>
                            <div class="info-item">
                                <label>Monto Pagado</label>
                                <span>$${parseFloat(pago.monto).toFixed(2)}</span>
                            </div>
                            <div class="info-item">
                                <label>Fecha del Pago</label>
                                <span>${new Date(pago.fechaPago).toLocaleDateString("es-ES")}</span>
                            </div>
                        `).join('')}
                    </div>
                </div>
            ` : ''}

            ${reservation.solicitudesEspeciales ? `
                <div class="info-section">
                    <h3><i class="fas fa-sticky-note"></i> Solicitudes Especiales</h3>
                    <p style="color: #666; line-height: 1.6;">${reservation.solicitudesEspeciales}</p>
                </div>
            ` : ""}

            <div class="actions">
                <a href="reservations.html" class="btn btn-primary">
                    <i class="fas fa-list"></i> Ver Mis Reservas
                </a>
                <button onclick="window.print()" class="btn btn-secondary">
                    <i class="fas fa-print"></i> Imprimir
                </button>
            </div>
        </div>

        <div class="footer">
            <p><strong>Agencia de Viajes Amanecer Tropical</strong></p>
            <p>Gracias por elegirnos para tu próxima aventura</p>
            <div class="contact">
                <a href="tel:+58XXXXXXXXX"><i class="fas fa-phone"></i> +58 XXX-XXXXXXX</a>
                <a href="mailto:info@amanecertropical.com"><i class="fas fa-envelope"></i> info@amanecertropical.com</a>
                <a href="#"><i class="fas fa-globe"></i> www.amanecertropical.com</a>
            </div>
        </div>
    `;
    }

    function showError(message) {
        const content = document.getElementById("content");
        content.innerHTML = `
                <div class="error">
                    <h2><i class="fas fa-exclamation-triangle"></i> Error</h2>
                    <p>${message}</p>
                    <div style="margin-top: 20px;">
                        <a href="index.html" class="btn btn-primary" style="display: inline-flex;">
                            <i class="fas fa-home"></i> Volver al Inicio
                        </a>
                    </div>
                </div>
            `;
    }

    function getStatusText(status) {
        const statusMap = {
            pendiente: "Pendiente",
            confirmado: "Confirmado",
            cancelado: "Cancelado",
            completado: "Completado",
        };
        return statusMap[status] || status;
    }

    function getStatusIcon(status) {
        const iconMap = {
            pendiente: "•",
            confirmado: "✓",
            cancelado: "✗",
            completado: "✓",
        };
        return iconMap[status] || "•";
    }

    function getServiceIcon(type) {
        const icons = {
            paquete: "suitcase",
            vuelo: "plane",
            hotel: "hotel",
            vehiculo: "car",
        };
        return icons[type] || "ticket-alt";
    }

    function getPaymentStatusText(status) {
        const statusMap = {
            pendiente: "Pendiente",
            completado: "Completado",
            fallido: "Fallido",
            reembolsado: "Reembolsado",
        };
        return statusMap[status] || status;
    }
