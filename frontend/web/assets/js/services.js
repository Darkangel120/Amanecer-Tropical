const API_BASE_URL = 'http://localhost:8080/api';

const PACKAGES_LIMIT = 10;
const HOTELS_LIMIT = 10;
const FLIGHTS_LIMIT = 15;
const VEHICLES_LIMIT = 8;

let packagesData, hotelsData, vehiclesData, flightsData;

let reservationItems = [];

let usdToVesRate = 36.50;

async function loadExchangeRate() {
    try {
        const response = await fetch(`${API_BASE_URL}/currency/rate`);
        if (response.ok) {
            const data = await response.json();
            usdToVesRate = data.usdToVesRate;
            console.log(`Exchange rate loaded: 1 USD = ${usdToVesRate} VES`);
            if (packagesData) displayPackages(packagesData);
            if (flightsData) displayFlights(flightsData);
            if (hotelsData) displayHotels(hotelsData);
            if (vehiclesData) displayVehicles(vehiclesData);
        } else {
            console.warn('Failed to load exchange rate, using default rate');
        }
    } catch (error) {
        console.error('Error loading exchange rate:', error);
    }
}

function formatPrice(usdPrice) {
    const usd = parseFloat(usdPrice);
    const ves = usd * usdToVesRate;
    return `$${usd.toFixed(2)} USD / Bs${ves.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

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

    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            this.classList.add('active');
            const tabId = this.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });

    const urlParams = new URLSearchParams(window.location.search);
    const destinationId = urlParams.get('destination');

    if (destinationId) {
        loadDestinationDetails(destinationId);
    }

    const bookingForm = document.querySelector('#booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await createReservation();
        });
    }

    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../index.html';
        });
    }

    loadPackages();
    loadFlights();
    loadHotels();
    loadVehicles();

    initializeReservationSidebar();

    initializeSidebarToggle();

    initializeQRButton();

    loadExchangeRate();
});

async function loadDestinationDetails(destinationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/packages/${destinationId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const destination = await response.json();
            displayDestinationDetails(destination);
        } else {
            console.error('Error loading destination details');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayDestinationDetails(destination) {
    const destinationTitle = document.querySelector('.destination-title');
    const destinationDescription = document.querySelector('.destination-description');
    const destinationPrice = document.querySelector('.destination-price');

    if (destinationTitle) destinationTitle.textContent = destination.nombre;
    if (destinationDescription) destinationDescription.textContent = destination.descripcion;
    if (destinationPrice) destinationPrice.textContent = formatPrice(destination.precio);

    const bookingForm = document.querySelector('#booking-form');
    if (bookingForm) {
        const destinationIdInput = bookingForm.querySelector('input[name="destinationId"]');
        if (destinationIdInput) {
            destinationIdInput.value = destination.id;
        }
    }
}

async function loadPackages() {
    try {
        const response = await fetch(`${API_BASE_URL}/packages`);
        if (response.ok) {
            const destinations = await response.json();
            packagesData = destinations;
            console.log(`Loaded ${packagesData.length} packages`);
            displayPackages(packagesData);
        } else {
            console.error('Error loading packages');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayPackages(destinations = packagesData) {
    const packagesGrid = document.getElementById('packages-grid');
    if (!packagesGrid) return;

    packagesGrid.innerHTML = '';

    if (destinations.length === 0) {
        packagesGrid.innerHTML = '<p>No hay paquetes disponibles en este momento.</p>';
        return;
    }

    destinations.forEach(destination => {
        const packageCard = document.createElement('div');
        packageCard.className = 'service-card';
        packageCard.innerHTML = `
            <img src="../${destination.urlImagen}" alt="Paquete ${destination.nombre}">
            <h3>Paquete ${destination.nombre}</h3>
            <p>${destination.descripcion}</p>
            <div class="service-price" style="display: flex; justify-content: center;">${formatPrice(destination.precio)}</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewPackageDetails(${destination.id})">Ver Detalles</a>
                <a href="#" class="btn" onclick="bookPackage(${destination.id})">Reservar Ahora</a>
            </div>
        `;
        packagesGrid.appendChild(packageCard);
    });
}

function viewPackageDetails(packageId) {
    const packageData = packagesData.find(pkg => pkg.id === packageId);
    if (!packageData) return;

    const modal = document.getElementById('package-modal');
    const modalImage = document.getElementById('modal-image');
    const modalTitle = document.getElementById('modal-title');
    const modalDescription = document.getElementById('modal-description');
    const modalPrice = document.getElementById('modal-price');
    const includesList = document.getElementById('includes-list');
    const itineraryList = document.getElementById('itinerary-list');

    modalImage.src = "../" + packageData.urlImagen;
    modalImage.alt = `Paquete ${packageData.nombre}`;
    modalTitle.textContent = `Paquete ${packageData.nombre}`;
    modalDescription.textContent = packageData.descripcion;
    modalPrice.textContent = formatPrice(packageData.precio);

    includesList.innerHTML = '';
    let includes = packageData.incluye;
    console.log('Includes data:', includes, typeof includes);
    if (typeof includes === 'string') {
        try {
            includes = JSON.parse(includes);
        } catch (e) {
            includes = includes.split(',').map(item => item.trim());
        }
    }
    console.log('Processed includes:', includes, Array.isArray(includes));
    if (Array.isArray(includes)) {
        includes.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            includesList.appendChild(li);
        });
    } else if (includes) {
        const li = document.createElement('li');
        li.textContent = includes;
        includesList.appendChild(li);
    }

    itineraryList.innerHTML = '';
    let itinerary = packageData.itinerario;
    console.log('Itinerary data:', itinerary, typeof itinerary);
    if (typeof itinerary === 'string') {
        try {
            itinerary = JSON.parse(itinerary);
        } catch (e) {
            itinerary = itinerary.split(',').map(item => item.trim());
        }
    }
    console.log('Processed itinerary:', itinerary, Array.isArray(itinerary));
    if (Array.isArray(itinerary)) {
        itinerary.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            itineraryList.appendChild(li);
        });
    } else if (itinerary) {
        const li = document.createElement('li');
        li.textContent = itinerary;
        itineraryList.appendChild(li);
    }

    modal.style.display = 'block';

    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function bookPackage(destinationId) {
    if (!packagesData) {
        showToast('Cargando datos de paquetes, por favor espere un momento.', 'warning');
        return;
    }
    addToReservation('package', destinationId);
}

async function loadFlights() {
    try {
        const response = await fetch(`${API_BASE_URL}/flights`);
        if (response.ok) {
            const flights = await response.json();
            flightsData = flights;
            console.log(`Loaded ${flightsData.length} flights`);
            displayFlights(flightsData);
            populateFlightFilters(flightsData);
        } else {
            console.error('Error loading flights');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayFlights(flights = flightsData) {
    const flightsGrid = document.getElementById('flights-grid');
    if (!flightsGrid) return;

    flightsGrid.innerHTML = '';

    if (flights.length === 0) {
        flightsGrid.innerHTML = '<p>No hay vuelos disponibles en este momento.</p>';
        return;
    }

    flights.forEach(flight => {
        const flightCard = document.createElement('div');
        flightCard.className = 'service-card';
        const departureDateTime = new Date(flight.departureTime).toLocaleString('es-ES');
        const arrivalDateTime = new Date(flight.arrivalTime).toLocaleString('es-ES');
        flightCard.innerHTML = `
            <h3>${flight.origen} - ${flight.destino}</h3>
            <ul class="flight-details">
                <li><strong>Aerolínea</strong> ${flight.aerolinea}</li>
                <li><strong>Fecha y Hora de Salida</strong> ${departureDateTime}</li>
                <li><strong>Fecha y Hora de Llegada</strong> ${arrivalDateTime} aprox</li>
            </ul>
            <div class="service-price" style="display: flex; justify-content: center;">${formatPrice(flight.precio)}</div>
            <a href="#" class="btn" onclick="addToReservation('flight', ${flight.id})" style="display: flex; justify-content: center;">Reservar Vuelo</a>
        `;
        flightsGrid.appendChild(flightCard);
    });
}

async function loadHotels() {
    try {
        const response = await fetch(`${API_BASE_URL}/hotels`);
        if (response.ok) {
            const hotels = await response.json();
            hotelsData = hotels;
            console.log(`Loaded ${hotelsData.length} hotels`);
            displayHotels(hotelsData);
            populateHotelDestinations(hotelsData);
        } else {
            console.error('Error loading hotels');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayHotels(hotels = hotelsData) {
    const hotelsGrid = document.getElementById('hotels-grid');
    if (!hotelsGrid) return;

    hotelsGrid.innerHTML = '';

    if (hotels.length === 0) {
        hotelsGrid.innerHTML = '<p>No hay hoteles disponibles en este momento.</p>';
        return;
    }

    hotels.forEach(hotel => {
        const hotelCard = document.createElement('div');
        hotelCard.className = 'service-card';
        const starsHtml = generateStars(hotel.estrellas);
        hotelCard.innerHTML = `
            <img src="../${hotel.urlImagen}" alt="${hotel.nombre}">
            <h3>${hotel.nombre}</h3>
            <div class="hotel-stars">${starsHtml}</div>
            <p>${hotel.descripcion}</p>
            <div class="service-price"  style="display: flex; justify-content: center;">${formatPrice(hotel.precioPorNoche)}/noche</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewHotelDetails(${hotel.id})">Ver Detalles</a>
                <a href="#" class="btn" onclick="addToReservation('hotel', ${hotel.id})">Reservar Hotel</a>
            </div>
        `;
        hotelsGrid.appendChild(hotelCard);
    });
}

function generateStars(stars) {
    let starsHtml = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= stars) {
            starsHtml += '<i class="fas fa-star"></i>';
        } else {
            starsHtml += '<i class="far fa-star"></i>';
        }
    }
    return starsHtml;
}

async function loadVehicles() {
    try {
        const response = await fetch(`${API_BASE_URL}/vehicles`);
        if (response.ok) {
            const vehicles = await response.json();
            vehiclesData = vehicles; 
            console.log(`Loaded ${vehiclesData.length} vehicles`); 
            displayVehicles(vehicles);
            populateVehicleTypes(vehicles);
        } else {
            console.error('Error loading vehicles');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayVehicles(vehicles = vehiclesData) {
    const vehiclesGrid = document.getElementById('vehicles-grid');
    if (!vehiclesGrid) return;

    vehiclesGrid.innerHTML = '';

    vehicles.forEach(vehicle => {
        const vehicleCard = document.createElement('div');
        vehicleCard.className = 'service-card';
        vehicleCard.innerHTML = `
            <h3>${vehicle.nombre}</h3>
            <p>${vehicle.ubicacion}</p>
            <p>${vehicle.descripcion}</p>
            <div class="service-price"  style="display: flex; justify-content: center;">${formatPrice(vehicle.precioPorDia)}/día</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewVehicleDetails(${vehicle.id})">Ver Detalles</a>
                <a href="#" class="btn" onclick="addToReservation('vehicle', ${vehicle.id})" style="font-size: 13px;">Alquilar Vehículo</a>
            </div>
        `;
        vehiclesGrid.appendChild(vehicleCard);
    });
}

function viewHotelDetails(hotelId) {
    const hotelData = hotelsData.find(hotel => hotel.id === hotelId);
    if (!hotelData) return;

    const modal = document.getElementById('hotel-modal');
    const modalImage = document.getElementById('hotel-modal-image');
    const modalTitle = document.getElementById('hotel-modal-title');
    const modalDescription = document.getElementById('hotel-modal-description');
    const modalPrice = document.getElementById('hotel-modal-price');
    const modalStars = document.getElementById('hotel-modal-stars');
    const amenitiesList = document.getElementById('amenities-list');

    modalImage.src = "../" + hotelData.urlImagen;
    modalImage.alt = hotelData.nombre;
    modalTitle.textContent = hotelData.nombre;
    modalDescription.textContent = hotelData.descripcion;
    modalPrice.textContent = `${formatPrice(hotelData.precioPorNoche)}/noche`;
    modalStars.innerHTML = generateStars(hotelData.estrellas);

    amenitiesList.innerHTML = '';
    try {
        const amenities = JSON.parse(hotelData.comodidades);
        amenities.forEach(amenity => {
            const li = document.createElement('li');
            li.textContent = amenity;
            amenitiesList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = hotelData.comodidades;
        amenitiesList.appendChild(li);
    }

    modal.style.display = 'block';

    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function viewVehicleDetails(vehicleId) {
    if (!vehiclesData) {
        showToast('Los datos del vehículo aún se están cargando. Por favor, espera un momento e intenta de nuevo.', 'warning');
        return;
    }
    const vehicleData = vehiclesData.find(vehicle => vehicle.id === vehicleId);
    if (!vehicleData) {
        showToast('Datos del vehículo no encontrados para ID: ' + vehicleId, 'error');
        return;
    }

    const modal = document.getElementById('vehicle-modal');
    const modalImage = document.getElementById('vehicle-modal-image');
    const modalTitle = document.getElementById('vehicle-modal-title');
    const modalDescription = document.getElementById('vehicle-modal-description');
    const modalPrice = document.getElementById('vehicle-modal-price');
    const vehicleCapacity = document.getElementById('vehicle-capacity');
    const vehicleTransmission = document.getElementById('vehicle-transmission');
    const vehicleFuel = document.getElementById('vehicle-fuel');

    modalImage.src = ".." + vehicleData.urlImagen;
    modalImage.alt = vehicleData.nombre;
    modalTitle.textContent = vehicleData.nombre;
    modalDescription.textContent = vehicleData.descripcion;
    modalPrice.textContent = `${formatPrice(vehicleData.precioPorDia)}/día`;
    vehicleCapacity.textContent = vehicleData.capacidad;
    vehicleTransmission.textContent = vehicleData.transmision;
    vehicleFuel.textContent = vehicleData.TipoCombustible;

    modal.style.display = 'block';

    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

async function createReservation() {
    const formData = new FormData(document.querySelector('#booking-form'));
    const user = JSON.parse(localStorage.getItem('user'));

    const reservationData = {
        usuario: { id: user.id },
        paquete: { id: parseInt(formData.get('destinationId')) },
        tipoServicio: 'paquete',
        fechaInicio: formData.get('checkInDate'),
        fechaFin: formData.get('checkOutDate'),
        numeroPersonas: parseInt(formData.get('numberOfGuests')),
        precioTotal: parseFloat(formData.get('totalPrice')),
        estado: 'pendiente',
        solicitudesEspeciales: formData.get('specialRequests') || ''
    };

    try {
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(reservationData)
        });

        if (response.ok) {
            showToast('Reserva creada exitosamente', 'success');
            document.getElementById('reservation-modal').style.display = 'none';
            window.location.href = 'reservations.html';
        } else {
            const error = await response.text();
            showToast('Error al crear la reserva: ' + error, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
}

function openReservationModal(destinationId) {
    const packageData = packagesData.find(pkg => pkg.id === destinationId);
    if (!packageData) return;

    const modal = document.getElementById('reservation-modal');
    const modalImage = document.getElementById('reservation-modal-image');
    const modalTitle = document.getElementById('reservation-modal-title');
    const modalDescription = document.getElementById('reservation-modal-description');
    const modalPrice = document.getElementById('reservation-modal-price');
    const includesList = document.getElementById('reservation-includes-list');
    const itineraryList = document.getElementById('reservation-itinerary-list');
    const destinationIdInput = document.getElementById('destinationId');
    const totalPriceInput = document.getElementById('totalPrice');

    modalImage.src = "../" + packageData.urlImagen;
    modalImage.alt = `Paquete ${packageData.nombre}`;
    modalTitle.textContent = `Reservar Paquete ${packageData.nombre}`;
    modalDescription.textContent = packageData.descripcion;
    modalPrice.textContent = formatPrice(packageData.precio);
    destinationIdInput.value = destinationId;
    totalPriceInput.value = packageData.precio;

    includesList.innerHTML = '';
    let includes = packageData.incluye;
    if (typeof includes === 'string') {
        try {
            includes = JSON.parse(includes);
        } catch (e) {
            includes = includes.split(',').map(item => item.trim());
        }
    }
    if (Array.isArray(includes)) {
        includes.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            includesList.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = includes;
        includesList.appendChild(li);
    }

    itineraryList.innerHTML = '';
    let itinerary = packageData.itinerario;
    if (typeof itinerary === 'string') {
        try {
            itinerary = JSON.parse(itinerary);
        } catch (e) {
            itinerary = itinerary.split(',').map(item => item.trim());
        }
    }
    if (Array.isArray(itinerary)) {
        itinerary.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            itineraryList.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = itinerary;
        itineraryList.appendChild(li);
    }

    modal.style.display = 'block';

    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function initializeReservationSidebar() {
    const clearBtn = document.getElementById('clear-reservation');
    const confirmBtn = document.getElementById('confirm-reservation');

    if (clearBtn) {
        clearBtn.addEventListener('click', clearReservation);
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', confirmReservation);
    }
}

function addToReservation(type, id) {
    let item = null;
    let price = 0;

    if (type === 'flight') {
        const flight = flightsData.find(f => f.id === id);
        item = {
            type: 'flight',
            id: flight.id,
            name: `${flight.origen} - ${flight.destino}`,
            description: `Aerolinea ${flight.aerolinea}`,
            price: flight.precio
        };
        price = flight.precio;
    } else if (type === 'hotel') {
        const hotel = hotelsData.find(h => h.id === id);
        if (hotel) {
            item = {
                type: 'hotel',
                id: hotel.id,
                name: hotel.nombre,
                description: hotel.descripcion,
                price: hotel.precioPorNoche
            };
            price = hotel.precioPorNoche;
        }
    } else if (type === 'vehicle') {
        const vehicle = vehiclesData.find(v => v.id === id);
        if (vehicle) {
            item = {
                type: 'vehicle',
                id: vehicle.id,
                name: vehicle.nombre,
                description: vehicle.descripcion,
                price: vehicle.precioPorDia
            };
            price = vehicle.precioPorDia;
        }
    } else if (type === 'package') {
        const packageData = packagesData.find(p => p.id === id);
        if (packageData) {
            item = {
                type: 'package',
                id: packageData.id,
                name: `Paquete ${packageData.nombre}`,
                description: packageData.descripcion,
                price: packageData.precio
            };
            price = packageData.precio;
        }
    }

    if (item) {
        const existingIndex = reservationItems.findIndex(r => r.type === type && r.id === id);
        if (existingIndex === -1) {
            reservationItems.push(item);
            updateReservationSidebar();
        } else {
            showToast('Este servicio ya está en tu reserva.', 'warning');
        }
    }
}

function removeFromReservation(index) {
    reservationItems.splice(index, 1);
    updateReservationSidebar();
}

function updateReservationSidebar() {
    const reservationItemsContainer = document.getElementById('reservation-items');
    const totalPriceElement = document.getElementById('total-price');
    const confirmBtn = document.getElementById('confirm-reservation');
    const cartCountElement = document.getElementById('cart-count');

    if (!reservationItemsContainer) return;

    reservationItemsContainer.innerHTML = '';

    let totalPrice = 0;

    const packages = reservationItems.filter(item => item.type === 'package');
    const otherServices = reservationItems.filter(item => item.type !== 'package');

    if (packages.length > 0) {
        const packagesSection = document.createElement('div');
        packagesSection.className = 'reservation-section';
        packagesSection.innerHTML = '<h4>📦 Paquetes</h4>';
        reservationItemsContainer.appendChild(packagesSection);

        packages.forEach((item) => {
            const itemElement = document.createElement('div');
            itemElement.className = 'reservation-item';
            itemElement.innerHTML = `
                <h4>${item.name}</h4>
                <p>${item.description.substring(0, 50)}...</p>
                <p class="price">${formatPrice(item.price)}</p>
                <button class="remove-item" onclick="removeFromReservation(${reservationItems.indexOf(item)})">×</button>
            `;
            packagesSection.appendChild(itemElement);
            totalPrice += parseFloat(item.price);
        });
    }

    if (otherServices.length > 0) {
        const servicesSection = document.createElement('div');
        servicesSection.className = 'reservation-section';
        servicesSection.innerHTML = '<h4> Reserva Personalizada</h4>';
        reservationItemsContainer.appendChild(servicesSection);

        otherServices.forEach((item) => {
            const icon = item.type === 'flight' ? '<i class="fa-solid fa-plane"></i>' : item.type === 'hotel' ? '<i class="fa-solid fa-hotel"></i>' : '<i class="fa-solid fa-car"></i>';
            const itemElement = document.createElement('div');
            itemElement.className = 'reservation-item';
            itemElement.innerHTML = `
                <h4>${icon} ${item.name}</h4>
                <p>${item.description.substring(0, 50)}...</p>
                <p class="price">${formatPrice(item.price)}</p>
                <button class="remove-item" onclick="removeFromReservation(${reservationItems.indexOf(item)})">×</button>
            `;
            servicesSection.appendChild(itemElement);
            totalPrice += parseFloat(item.price);
        });

        if (otherServices.length > 1) {
            const compatibility = checkServicesCompatibilityImproved(otherServices);
            const infoElement = document.createElement('div');
            infoElement.className = 'custom-reservation-info';
            infoElement.style.cssText = compatibility.compatible 
                ? 'background: #d4edda; padding: 10px; margin-top: 10px; border-radius: 5px; font-size: 12px; border: 1px solid #c3e6cb;'
                : 'background: #fff3cd; padding: 10px; margin-top: 10px; border-radius: 5px; font-size: 12px; border: 1px solid #ffc107;';
            
            if (compatibility.compatible) {
                infoElement.innerHTML = `
                    <p style="margin: 0;"><strong>✓ Servicios Compatibles</strong></p>
                    <p style="margin: 5px 0 0 0;">Lugar: ${compatibility.location}</p>
                    <p style="margin: 5px 0 0 0; font-size: 11px;">Se crearán como reserva combinada</p>
                `;
            } else {
                infoElement.innerHTML = `
                    <p style="margin: 0;"><strong>⚠️ Ubicaciones Diferentes</strong></p>
                    <p style="margin: 5px 0 0 0; font-size: 11px;">Se crearán reservas separadas</p>
                `;
            }
            servicesSection.appendChild(infoElement);
        }
    }

    if (totalPriceElement) {
        totalPriceElement.textContent = formatPrice(totalPrice.toFixed(2));
    }

    if (cartCountElement) {
        cartCountElement.textContent = reservationItems.length;
    }

    if (confirmBtn) {
        confirmBtn.disabled = reservationItems.length === 0;
    }
}

function clearReservation() {
    reservationItems = [];
    updateReservationSidebar();
}

function confirmReservation() {
    if (reservationItems.length === 0) {
        showToast('No hay servicios en la reserva.', 'warning');
        return;
    }

    showReservationConfirmationModal();
}

function showReservationConfirmationModal() {
    const modal = document.getElementById('reservation-confirm-modal');
    if (!modal) return;

    const totalPrice = reservationItems.reduce((sum, item) => sum + parseFloat(item.price), 0);

    const reservationDetails = document.getElementById('reservation-details');
    const reservationTotal = document.getElementById('reservation-total');

    if (reservationDetails) {
        reservationDetails.innerHTML = '';

        const packages = reservationItems.filter(item => item.type === 'package');
        const otherServices = reservationItems.filter(item => item.type !== 'package');

        if (packages.length > 0) {
            const packagesSection = document.createElement('div');
            packagesSection.innerHTML = '<h4>📦 Paquetes</h4>';
            packages.forEach(item => {
                const itemDiv = document.createElement('div');
                itemDiv.className = 'reservation-item-detail';
                itemDiv.innerHTML = `
                    <span>${item.name}</span>
                    <span>${formatPrice(item.price)}</span>
                `;
                packagesSection.appendChild(itemDiv);
            });
            reservationDetails.appendChild(packagesSection);
        }

        if (otherServices.length > 0) {
            const servicesSection = document.createElement('div');
            servicesSection.innerHTML = '<h4>🔧 Servicios Personalizados</h4>';
            otherServices.forEach(item => {
                const icon = item.type === 'flight' ? '✈️' : item.type === 'hotel' ? '🏨' : '🚗';
                const itemDiv = document.createElement('div');
                itemDiv.className = 'reservation-item-detail';
                itemDiv.innerHTML = `
                    <span>${icon} ${item.name}</span>
                    <span>${formatPrice(item.price)}</span>
                `;
                servicesSection.appendChild(itemDiv);
            });
            reservationDetails.appendChild(servicesSection);
        }
    }

    if (reservationTotal) {
        reservationTotal.textContent = formatPrice(totalPrice.toFixed(2));
    }

    const reservationForm = document.getElementById('reservation-form');
    if (reservationForm) {
        reservationForm.onsubmit = function(e) {
            e.preventDefault();
            modal.style.display = 'none';
            const formData = new FormData(reservationForm);
            const reservationData = {
                fechaInicio: formData.get('fecha-inicio'),
                fechaFin: formData.get('fecha-fin'),
                numeroPersonas: parseInt(formData.get('numero-personas')),
                solicitudesEspeciales: formData.get('solicitudes-especiales') || ''
            };
            showPaymentModal(totalPrice, reservationData);
        };
    }

    const cancelBtn = document.getElementById('cancel-reservation-btn');
    if (cancelBtn) {
        cancelBtn.onclick = function() {
            modal.style.display = 'none';
        };
    }

    const closeBtn = document.getElementById('reservation-confirm-close');
    if (closeBtn) {
        closeBtn.onclick = function() {
            modal.style.display = 'none';
        };
    }

    modal.style.display = 'block';

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function showPaymentModal(totalAmount, reservationData) {
    const modal = document.getElementById('payment-modal');
    if (!modal) return;

    const paymentSummary = document.getElementById('payment-summary');
    if (paymentSummary) {
        paymentSummary.innerHTML = '';

        const summaryDiv = document.createElement('div');
        summaryDiv.innerHTML = `
            <h3>Resumen de la Reserva</h3>
            <p><strong>Fecha:</strong> ${reservationData.fechaInicio} - ${reservationData.fechaFin}</p>
            <p><strong>Número de Personas:</strong> ${reservationData.numeroPersonas}</p>
            ${reservationData.solicitudesEspeciales ? `<p><strong>Solicitudes Especiales:</strong> ${reservationData.solicitudesEspeciales}</p>` : ''}
        `;
        paymentSummary.appendChild(summaryDiv);

        const itemsDiv = document.createElement('div');
        itemsDiv.innerHTML = '<h4>Servicios Reservados:</h4>';
        reservationItems.forEach(item => {
            const itemDiv = document.createElement('div');
            itemDiv.className = 'payment-item';
            itemDiv.innerHTML = `
                <span>${item.name}</span>
                <span>${formatPrice(item.price)}</span>
            `;
            itemsDiv.appendChild(itemDiv);
        });
        paymentSummary.appendChild(itemsDiv);
    }

    const totalAmountElement = document.getElementById('payment-total');
    if (totalAmountElement) {
        totalAmountElement.textContent = formatPrice(totalAmount.toFixed(2));
    }

    const paymentForm = document.getElementById('payment-form');
    if (paymentForm) {
        paymentForm.onsubmit = async function(e) {
            e.preventDefault();
            await processPayment(totalAmount, reservationData);
        };
    }

    const closeBtn = modal.querySelector('.close');
    if (closeBtn) {
        closeBtn.onclick = function() {
            modal.style.display = 'none';
        };
    }

    const paymentMethodSelect = document.getElementById('payment-method');
    if (paymentMethodSelect) {
        paymentMethodSelect.addEventListener('change', function() {
            const selectedMethod = this.value;
            const transferenciaDetails = document.getElementById('transferencia-details');
            const pagomovilDetails = document.getElementById('pagomovil-details');
            const paymentReferenceGroup = document.getElementById('payment-reference-group');

            if (transferenciaDetails) transferenciaDetails.style.display = 'none';
            if (pagomovilDetails) pagomovilDetails.style.display = 'none';
            if (paymentReferenceGroup) paymentReferenceGroup.style.display = 'none';

            if (selectedMethod === 'transferencia' && transferenciaDetails) {
                transferenciaDetails.style.display = 'block';
                paymentReferenceGroup.style.display = 'block';
            } else if (selectedMethod === 'pagomovil' && pagomovilDetails) {
                pagomovilDetails.style.display = 'block';
                paymentReferenceGroup.style.display = 'block';
            }
        });
    }

    const backToConfirmationBtn = document.getElementById('back-to-confirmation');
    if (backToConfirmationBtn) {
        backToConfirmationBtn.addEventListener('click', function() {
            modal.style.display = 'none';
            showReservationConfirmationModal();
        });
    }

    modal.style.display = 'block';

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

async function processPayment(totalAmount, reservationData) {
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user || !user.id) {
        showToast('Error: Usuario no autenticado', 'error');
        return;
    }

    const reservationResult = await createReservationsAfterPayment(user.id, totalAmount, reservationData);

    if (!reservationResult || !reservationResult.success) {
        showToast('Error al crear las reservas. No se procesará el pago.', 'error');
        return;
    }

    const paymentForm = document.getElementById('payment-form');
    const formData = new FormData(paymentForm);

    console.log('Form data entries:');
    for (let [key, value] of formData.entries()) {
        console.log(key, value);
    }

    const paymentMethod = formData.get('payment-method');
    console.log('Payment method value:', paymentMethod, 'Type:', typeof paymentMethod);

    const paymentData = {
        metodoPago: paymentMethod,
        referenciaPago: formData.get('payment-reference'),
        datosPago: JSON.stringify({
            cardNumber: formData.get('card-number'),
            expiryDate: formData.get('expiry-date'),
            cvv: formData.get('cvv'),
            cardHolder: formData.get('card-holder')
        }),
        monto: totalAmount,
        reservationIds: reservationResult.reservationIds
    };

    console.log('Constructed payment data:', paymentData);

    try {
        console.log('Sending payment data:', paymentData);

        const paymentResponse = await fetch(`${API_BASE_URL}/payments/process`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(paymentData)
        });

        if (!paymentResponse.ok) {
            const errorText = await paymentResponse.text();
            console.error('Payment processing failed:', paymentResponse.status, errorText);
            throw new Error(`Error al procesar el pago: ${errorText}`);
        }

        const paymentResult = await paymentResponse.json();

        document.getElementById('payment-modal').style.display = 'none';

        showToast('¡Pago procesado exitosamente! Su reserva ha sido confirmada.', 'success');
        clearReservation();
        window.location.href = 'reservations.html';

    } catch (error) {
        console.error('Error:', error);
        showToast('Error al procesar el pago: ' + error.message, 'error');
    }
}

async function createReservationsAfterPayment(userId, totalAmount, reservationData) {
    const packages = reservationItems.filter(item => item.type === 'package');
    const customServices = reservationItems.filter(item => item.type !== 'package');



    const fechaInicio = reservationData.fechaInicio;
    const fechaFin = reservationData.fechaFin;
    const numeroPersonas = reservationData.numeroPersonas;

    const inicio = new Date(fechaInicio);
    const fin = new Date(fechaFin);
    if (fin <= inicio) {
        throw new Error('La fecha de fin debe ser posterior a la fecha de inicio');
    }

    let successCount = 0;
    let errorMessages = [];
    let reservationIds = [];

    try {
        for (const packageItem of packages) {
            try {
                const reservationData = {
                    usuario: { id: userId },
                    paquete: { id: packageItem.id },
                    tipoServicio: 'paquete',
                    fechaInicio: fechaInicio,
                    fechaFin: fechaFin,
                    numeroPersonas: numeroPersonas,
                    precioTotal: parseFloat(packageItem.price),
                    estado: 'pendiente',
                    solicitudesEspeciales: `Paquete: ${packageItem.name}`
                };

                const response = await fetch(`${API_BASE_URL}/reservations`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify(reservationData)
                });

                if (response.ok) {
                    const result = await response.json();
                    reservationIds.push(result.id);
                    successCount++;
                } else {
                    const error = await response.text();
                    errorMessages.push(`Paquete ${packageItem.name}: ${error}`);
                }
            } catch (error) {
                errorMessages.push(`Paquete ${packageItem.name}: ${error.message}`);
            }
        }

        if (customServices.length > 0) {
            const compatibility = checkServicesCompatibilityImproved(customServices);

            console.log('Compatibilidad de servicios:', compatibility);

            if (compatibility.compatible && customServices.length > 1) {
                try {
                    const totalPrice = customServices.reduce((sum, service) => sum + parseFloat(service.price), 0);
                    const servicesList = [];
                    if (customServices.filter(s => s.type === 'flight').length > 0) servicesList.push(`${customServices.filter(s => s.type === 'flight').length} vuelo(s)`);
                    if (customServices.filter(s => s.type === 'hotel').length > 0) servicesList.push(`${customServices.filter(s => s.type === 'hotel').length} hotel(es)`);
                    if (customServices.filter(s => s.type === 'vehicle').length > 0) servicesList.push(`${customServices.filter(s => s.type === 'vehicle').length} vehículo(s)`);

                    const description = `Reserva Personalizada en ${compatibility.location}: ${servicesList.join(', ')}. Servicios incluidos: ${customServices.map(s => s.name).join(', ')}`;

                    const serviceRefs = {
                        vuelo: customServices.find(s => s.type === 'flight') ? { id: customServices.find(s => s.type === 'flight').id } : null,
                        hotel: customServices.find(s => s.type === 'hotel') ? { id: customServices.find(s => s.type === 'hotel').id } : null,
                        vehiculo: customServices.find(s => s.type === 'vehicle') ? { id: customServices.find(s => s.type === 'vehicle').id } : null,
                        paquete: null
                    };

                    const reservationData = {
                        usuario: { id: userId },
                        ...serviceRefs,
                        tipoServicio: 'personalizado',
                        fechaInicio: fechaInicio,
                        fechaFin: fechaFin,
                        numeroPersonas: numeroPersonas,
                        precioTotal: parseFloat(totalPrice.toFixed(2)),
                        estado: 'pendiente',
                        solicitudesEspeciales: description
                    };

                    const response = await fetch(`${API_BASE_URL}/reservations`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${localStorage.getItem('token')}`
                        },
                        body: JSON.stringify(reservationData)
                    });

                    if (response.ok) {
                        const result = await response.json();
                        reservationIds.push(result.id);
                        successCount++;
                    } else {
                        const error = await response.text();
                        errorMessages.push(`Reserva personalizada: ${error}`);
                    }
                } catch (error) {
                    errorMessages.push(`Reserva personalizada: ${error.message}`);
                }
            } else {
                if (!compatibility.compatible) {
                    showToast('Los servicios tienen ubicaciones diferentes. Se crearán reservaciones separadas.', 'warning');
                }

                for (const service of customServices) {
                    try {
                        let serviceType = '';
                        let serviceRef = {};

                        switch(service.type) {
                            case 'flight':
                                serviceType = 'vuelo';
                                serviceRef = { vuelo: { id: service.id } };
                                break;
                            case 'hotel':
                                serviceType = 'hotel';
                                serviceRef = { hotel: { id: service.id } };
                                break;
                            case 'vehicle':
                                serviceType = 'vehiculo';
                                serviceRef = { vehiculo: { id: service.id } };
                                break;
                        }

                        const reservationData = {
                            usuario: { id: userId },
                            ...serviceRef,
                            tipoServicio: serviceType,
                            fechaInicio: fechaInicio,
                            fechaFin: fechaFin,
                            numeroPersonas: numeroPersonas,
                            precioTotal: parseFloat(service.price),
                            estado: 'pendiente',
                            solicitudesEspeciales: `Servicio: ${service.name}`
                        };

                        const response = await fetch(`${API_BASE_URL}/reservations`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': `Bearer ${localStorage.getItem('token')}`
                            },
                            body: JSON.stringify(reservationData)
                        });

                        if (response.ok) {
                            const result = await response.json();
                            reservationIds.push(result.id);
                            successCount++;
                        } else {
                            const error = await response.text();
                            errorMessages.push(`${service.name}: ${error}`);
                        }
                    } catch (error) {
                        errorMessages.push(`${service.name}: ${error.message}`);
                    }
                }
            }
        }

        if (errorMessages.length > 0) {
            throw new Error(`Errores al crear reservas: ${errorMessages.join(', ')}`);
        }

        return { success: true, reservationIds: reservationIds };
    } catch (error) {
        console.error('Error creating reservations:', error);
        return { success: false, error: error.message };
    }
}

async function createSingleReservation(userId, tipoServicio, serviceRef, fechaInicio, fechaFin, numeroPersonas, precio, descripcion) {
    const reservationData = {
        usuario: { id: userId },
        ...serviceRef,
        paquete: serviceRef.paquete || null,
        vuelo: serviceRef.vuelo || null,
        hotel: serviceRef.hotel || null,
        vehiculo: serviceRef.vehiculo || null,
        tipoServicio: tipoServicio,
        fechaInicio: fechaInicio,
        fechaFin: fechaFin,
        numeroPersonas: numeroPersonas,
        precioTotal: parseFloat(precio),
        estado: 'pendiente',
        solicitudesEspeciales: descripcion
    };

    try {
        console.log('Enviando reservación individual:', JSON.stringify(reservationData, null, 2));
        
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reservationData)
        });

        if (response.ok) {
            const result = await response.json();
            console.log('Reserva creada:', result);
            return { ok: true, data: result };
        } else {
            let errorText;
            try {
                const errorJson = await response.json();
                errorText = errorJson.message || JSON.stringify(errorJson);
            } catch (e) {
                errorText = await response.text();
            }
            console.error('Error del servidor:', errorText);
            return { ok: false, error: errorText };
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        return { ok: false, error: error.message };
    }
}

function checkServicesCompatibilityImproved(services) {
    const flights = services.filter(s => s.type === 'flight');
    const hotels = services.filter(s => s.type === 'hotel');
    const vehicles = services.filter(s => s.type === 'vehicle');

    if (services.length === 1) {
        const service = services[0];
        let location = 'Ubicación del servicio';
        
        if (service.type === 'flight' && flightsData) {
            const flight = flightsData.find(f => f.id === service.id);
            if (flight) location = extractCity(flight.destino);
        } else if (service.type === 'hotel' && hotelsData) {
            const hotel = hotelsData.find(h => h.id === service.id);
            if (hotel) location = extractCity(hotel.ubicacion);
        } else if (service.type === 'vehicle' && vehiclesData) {
            const vehicle = vehiclesData.find(v => v.id === service.id);
            if (vehicle && vehicle.ubicacion) location = extractCity(vehicle.ubicacion);
        }
        
        return { compatible: true, location: location };
    }

    let commonCity = null;
    let locationDetails = [];

    if (flights.length > 0 && flightsData) {
        for (const flightItem of flights) {
            const flight = flightsData.find(f => f.id === flightItem.id);
            if (flight) {
                const city = extractCity(flight.destino);
                locationDetails.push(`Vuelo: ${city} (${flight.destino})`);
                
                if (!commonCity) {
                    commonCity = city;
                } else if (!citiesMatch(commonCity, city)) {
                    return {
                        compatible: false,
                        reason: 'Las ciudades de destino no coinciden',
                        details: locationDetails.join('\n'),
                        commonCity: null
                    };
                }
            }
        }
    }

    if (hotels.length > 0 && hotelsData) {
        for (const hotelItem of hotels) {
            const hotel = hotelsData.find(h => h.id === hotelItem.id);
            if (hotel) {
                const city = extractCity(hotel.ubicacion);
                locationDetails.push(`Hotel: ${city} (${hotel.ubicacion})`);
                
                if (!commonCity) {
                    commonCity = city;
                } else if (!citiesMatch(commonCity, city)) {
                    return {
                        compatible: false,
                        reason: 'Las ciudades no coinciden',
                        details: locationDetails.join('\n'),
                        commonCity: null
                    };
                }
            }
        }
    }

    if (vehicles.length > 0 && vehiclesData) {
        for (const vehicleItem of vehicles) {
            const vehicle = vehiclesData.find(v => v.id === vehicleItem.id);
            if (vehicle) {
                if (vehicle.ubicacion) {
                    const city = extractCity(vehicle.ubicacion);
                    locationDetails.push(`Vehículo: ${city} (${vehicle.ubicacion})`);
                    
                    if (!commonCity) {
                        commonCity = city;
                    } else if (!citiesMatch(commonCity, city)) {
                        return {
                            compatible: false,
                            reason: 'Las ciudades no coinciden',
                            details: locationDetails.join('\n'),
                            commonCity: null
                        };
                    }
                } else {
                    locationDetails.push(`Vehículo: ${vehicle.nombre} (Compatible con ${commonCity || 'cualquier ubicación'})`);
                }
            }
        }
    }

    return { 
        compatible: true, 
        location: commonCity || 'Ubicación combinada',
        details: locationDetails.join('\n'),
        services: {
            flights: flights.length,
            hotels: hotels.length,
            vehicles: vehicles.length
        }
    };
}

function extractCity(location) {
    if (!location) return '';
    
    const citys = location.split(',')[0].trim();
    const city = citys.split('(')[0].trim();
    return city;
}

async function createCustomReservation(userId, services, fechaInicio, fechaFin, numeroPersonas, location) {
    const flights = services.filter(s => s.type === 'flight');
    const hotels = services.filter(s => s.type === 'hotel');
    const vehicles = services.filter(s => s.type === 'vehicle');

    const totalPrice = services.reduce((sum, service) => sum + parseFloat(service.price), 0);

    const servicesList = [];
    if (flights.length > 0) servicesList.push(`${flights.length} vuelo(s)`);
    if (hotels.length > 0) servicesList.push(`${hotels.length} hotel(es)`);
    if (vehicles.length > 0) servicesList.push(`${vehicles.length} vehículo(s)`);

    const description = `Reserva Personalizada en ${location}: ${servicesList.join(', ')}. ` +
                       `Servicios incluidos: ${services.map(s => s.name).join(', ')}`;

    let serviceRefs = {
        vuelo: flights.length > 0 ? { id: flights[0].id } : null,
        hotel: hotels.length > 0 ? { id: hotels[0].id } : null,
        vehiculo: vehicles.length > 0 ? { id: vehicles[0].id } : null,
        paquete: null
    };

    const reservationData = {
        usuario: { id: userId },
        paquete: serviceRefs.paquete,
        vuelo: serviceRefs.vuelo,
        hotel: serviceRefs.hotel,
        vehiculo: serviceRefs.vehiculo,
        tipoServicio: 'personalizado',
        fechaInicio: fechaInicio,
        fechaFin: fechaFin,
        numeroPersonas: numeroPersonas,
        precioTotal: parseFloat(totalPrice.toFixed(2)),
        estado: 'pendiente',
        solicitudesEspeciales: description
    };

    try {
        console.log('Enviando reservación personalizada:', JSON.stringify(reservationData, null, 2));
        
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reservationData)
        });

        if (response.ok) {
            const result = await response.json();
            console.log('Reserva personalizada creada:', result);
            
            showToast(`✓ Reserva personalizada creada exitosamente! Lugar: ${location}, Total: $${totalPrice.toFixed(2)}, Servicios: ${servicesList.join(', ')}`, 'success');
            
            return { ok: true, data: result };
        } else {
            let errorText;
            try {
                const errorJson = await response.json();
                errorText = errorJson.message || JSON.stringify(errorJson);
            } catch (e) {
                errorText = await response.text();
            }
            console.error('Error del servidor:', errorText);
            return { ok: false, error: errorText };
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        return { ok: false, error: error.message };
    }
}

function citiesMatch(city1, city2) {
    if (!city1 || !city2) return false;
    
    const c1 = city1.toLowerCase().trim();
    const c2 = city2.toLowerCase().trim();
    
    return c1 === c2;
}

function showReservationResults(successCount, errorMessages) {
    if (successCount > 0 && errorMessages.length === 0) {
        showToast(`¡Éxito! Se crearon ${successCount} reservación(es) con pago procesado`, 'success');
        clearReservation();
        window.location.href = 'reservations.html';
    } else if (successCount > 0) {
        const confirmNav = confirm(
            `Se crearon ${successCount} reservación(es) con pago procesado, pero hubo ${errorMessages.length} error(es):\n\n${errorMessages.join('\n\n')}\n\n¿Deseas ver tus reservaciones?`
        );
        if (confirmNav) {
            clearReservation();
            window.location.href = 'reservations.html';
        }
    } else {
        showToast(`No se pudo crear ninguna reserva. Errores:\n\n${errorMessages.join('\n\n')}`, 'error');
    }
}

function initializeSidebarToggle() {
    const toggleBtn = document.getElementById('toggle-sidebar');
    const sidebar = document.getElementById('reservation-sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }
}

function populateFlightFilters(flights) {
    const originSelect = document.getElementById('flights-origin');
    const destinationSelect = document.getElementById('flights-destination');

    if (!originSelect || !destinationSelect) return;

    const origins = [...new Set(flights.map(flight => flight.origen))];
    const destinations = [...new Set(flights.map(flight => flight.destino))];

    origins.forEach(origin => {
        const option = document.createElement('option');
        option.value = origin;
        option.textContent = origin;
        originSelect.appendChild(option);
    });

    destinations.forEach(destination => {
        const option = document.createElement('option');
        option.value = destination;
        option.textContent = destination;
        destinationSelect.appendChild(option);
    });
}

function populateHotelDestinations(hotelsData) {
    const hotelUbicationSelect = document.getElementById('hotels-destination');

    if (!hotelUbicationSelect) return;

    const ubications = [...new Set(hotelsData.map(hotel => hotel.ubicacion))];

    ubications.forEach(ubication => {
        const option = document.createElement('option');
        option.value = ubication;
        option.textContent = ubication;
        hotelUbicationSelect.appendChild(option);
    });
}

function populateVehicleTypes(vehicles) {
    const vehicleTypeSelect = document.getElementById('vehicles-type');

    if (!vehicleTypeSelect) return;

    const types = [...new Set(vehicles.map(vehicle => vehicle.tipo))];

    types.forEach(type => {
        const option = document.createElement('option');
        option.value = type;
        option.textContent = type;
        vehicleTypeSelect.appendChild(option);
    });
}

document.addEventListener('DOMContentLoaded', function() {

    const packagesFilterBtn = document.getElementById('packages-filter-btn');
    const flightsFilterBtn = document.getElementById('flights-filter-btn');
    const hotelsFilterBtn = document.getElementById('hotels-filter-btn');
    const vehiclesFilterBtn = document.getElementById('vehicles-filter-btn');

    if (packagesFilterBtn) {
        packagesFilterBtn.addEventListener('click', filterPackages);
    }

    if (flightsFilterBtn) {
        flightsFilterBtn.addEventListener('click', filterFlights);
    }

    if (hotelsFilterBtn) {
        hotelsFilterBtn.addEventListener('click', filterHotels);
    }

    if (vehiclesFilterBtn) {
        vehiclesFilterBtn.addEventListener('click', filterVehicles);
    }
});

function filterPackages() {
    const searchInput = document.getElementById('packages-search');
    if (!searchInput || !packagesData) return;

    const searchTerm = searchInput.value.toLowerCase();
    const filteredPackages = packagesData.filter(pkg =>
        pkg.nombre.toLowerCase().includes(searchTerm) ||
        pkg.descripcion.toLowerCase().includes(searchTerm)
    );

    displayPackages(filteredPackages);
}

function filterFlights() {
    const originSelect = document.getElementById('flights-origin');
    const destinationSelect = document.getElementById('flights-destination');

    if (!originSelect || !destinationSelect || !flightsData) return;

    const selectedOrigin = originSelect.value;
    const selectedDestination = destinationSelect.value;

    const filteredFlights = flightsData.filter(flight => {
        const matchesOrigin = !selectedOrigin || flight.origen === selectedOrigin;
        const matchesDestination = !selectedDestination || flight.destino === selectedDestination;
        return matchesOrigin && matchesDestination;
    });

    displayFlights(filteredFlights);
}

function filterHotels() {
    const nameInput = document.getElementById('hotels-name');
    const ubicationSelect = document.getElementById('hotels-destination');

    if (!nameInput || !ubicationSelect) return;

    const searchTerm = nameInput.value.toLowerCase();
    const selectedUbication = ubicationSelect.value;

    const filteredHotels = hotelsData.filter(hotel => {
        const matchesName = !searchTerm || hotel.nombre.toLowerCase().includes(searchTerm);
        const matchesUbication = !selectedUbication || hotel.ubicacion.includes(selectedUbication);
        return matchesName && matchesUbication;
    });

    displayHotels(filteredHotels);
}

function filterVehicles() {
    const typeSelect = document.getElementById('vehicles-type');

    if (!typeSelect || !vehiclesData) return;

    const selectedType = typeSelect.value;

    const filteredVehicles = vehiclesData.filter(vehicle =>
        !selectedType || vehicle.tipo === selectedType
    );

    displayVehicles(filteredVehicles);
}

function initializeQRButton() {
    const qrButton = document.getElementById('qr-button');

    if (qrButton) {
        qrButton.addEventListener('click', function() {
            const currentUrl = window.location.href;
            generateQRCode(currentUrl);
        });
    }
}

function generateQRCode(url) {
    const modal = document.createElement('div');
    modal.id = 'qr-modal';
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content" style="text-align: center; max-width: 400px;">
            <span class="close" style="float: right; font-size: 28px; font-weight: bold; cursor: pointer;">&times;</span>
            <h2>Código QR</h2>
            <p>Escanea este código QR para compartir la página de servicios</p>
            <div id="qrcode" style="display: flex; justify-content: center; margin: 20px 0;"></div>
            <p style="font-size: 14px; color: #666;">URL: ${url}</p>
        </div>
    `;

    document.body.appendChild(modal);

    if (typeof QRCode !== 'undefined') {
        new QRCode(document.getElementById('qrcode'), {
            text: url,
            width: 256,
            height: 256,
            colorDark: '#000000',
            colorLight: '#ffffff',
            correctLevel: QRCode.CorrectLevel.H
        });
    } else {
        document.getElementById('qrcode').innerHTML = '<p>QR Code library not loaded</p>';
    }

    modal.style.display = 'block';

    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
        document.body.removeChild(modal);
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
            document.body.removeChild(modal);
        }
    };
}
