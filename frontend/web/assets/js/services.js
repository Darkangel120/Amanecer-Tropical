// Services JavaScript
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables for storing data
let packagesData, hotelsData, vehiclesData, flightsData;

// Reservation sidebar data
let reservationItems = [];

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

    // Get destination from URL params
    const urlParams = new URLSearchParams(window.location.search);
    const destinationId = urlParams.get('destination');

    if (destinationId) {
        loadDestinationDetails(destinationId);
    }

    // Booking form
    const bookingForm = document.querySelector('#booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await createReservation();
        });
    }

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

    // Load dynamic data for all tabs
    loadPackages();
    loadFlights();
    loadHotels();
    loadVehicles();

    // Initialize reservation sidebar
    initializeReservationSidebar();

    // Initialize sidebar toggle
    initializeSidebarToggle();
});

async function loadDestinationDetails(destinationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/destinations/${destinationId}`, {
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

    if (destinationTitle) destinationTitle.textContent = destination.name;
    if (destinationDescription) destinationDescription.textContent = destination.description;
    if (destinationPrice) destinationPrice.textContent = `$${destination.price}`;

    // Set destination ID in form
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
        const response = await fetch(`${API_BASE_URL}/destinations`);
        if (response.ok) {
            const destinations = await response.json();
            packagesData = destinations; // Store data globally
            displayPackages(destinations);
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
            <img src="../${destination.imageUrl}" alt="Paquete ${destination.name}">
            <h3>Paquete ${destination.name}</h3>
            <p>${destination.description}</p>
            <div class="service-price" style="display: flex; justify-content: center;">$${destination.price}</div>
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

    modalImage.src = packageData.imageUrl;
    modalImage.alt = `Paquete ${packageData.name}`;
    modalTitle.textContent = `Paquete ${packageData.name}`;
    modalDescription.textContent = packageData.description;
    modalPrice.textContent = `$${packageData.price}`;

    // Populate includes
    includesList.innerHTML = '';
    try {
        const includes = JSON.parse(packageData.includes);
        includes.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            includesList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = packageData.includes;
        includesList.appendChild(li);
    }

    // Populate itinerary
    itineraryList.innerHTML = '';
    try {
        const itinerary = JSON.parse(packageData.itinerary);
        itinerary.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            itineraryList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = packageData.itinerary;
        itineraryList.appendChild(li);
    }

    modal.style.display = 'block';

    // Close modal functionality
    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function bookPackage(destinationId) {
    // Check if packages data is loaded
    if (!packagesData) {
        alert('Cargando datos de paquetes, por favor espere un momento.');
        return;
    }
    // Add package to reservation sidebar like other services
    addToReservation('package', destinationId);
}

async function loadFlights() {
    try {
        const response = await fetch(`${API_BASE_URL}/flights`);
        if (response.ok) {
            const flights = await response.json();
            flightsData = flights; // Store data globally
            displayFlights(flights);
            populateFlightFilters(flights);
            populateHotelDestinations(flights);
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
            <h3>${flight.origin} - ${flight.destination}</h3>
            <ul class="flight-details">
                <li><strong>Aerolínea</strong> ${flight.airline}</li>
                <li><strong>Fecha y Hora de Salida</strong> ${departureDateTime}</li>
                <li><strong>Fecha y Hora de Llegada</strong> ${arrivalDateTime} aprox</li>
            </ul>
            <div class="service-price" style="display: flex; justify-content: center;">$${flight.price}</div>
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
            hotelsData = hotels; // Store data globally
            displayHotels(hotels);
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
        const starsHtml = generateStars(hotel.stars);
        hotelCard.innerHTML = `
            <img src="${hotel.imageUrl}" alt="${hotel.name}">
            <h3>${hotel.name}</h3>
            <div class="hotel-stars">${starsHtml}</div>
            <p>${hotel.description}</p>
            <div class="service-price"  style="display: flex; justify-content: center;">$${hotel.pricePerNight}/noche</div>
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
            vehiclesData = vehicles; // Store data globally
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
            <h3>${vehicle.name}</h3>
            <p>${vehicle.description}</p>
            <div class="service-price"  style="display: flex; justify-content: center;" style="display: flex; justify-content: center;">$${vehicle.pricePerDay}/día</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewVehicleDetails(${vehicle.id})">Ver Detalles</a>
                <a href="#" class="btn" onclick="addToReservation('vehicle', ${vehicle.id})" style="font-size: 13px;">Alquilar Vehículo</a>
            </div>
        `;
        vehiclesGrid.appendChild(vehicleCard);
    });
}

function viewHotelDetails(hotelId) {
    // For now, we'll use the hotels data loaded globally
    // In a real app, you might want to fetch individual hotel details
    const hotelData = hotelsData.find(hotel => hotel.id === hotelId);
    if (!hotelData) return;

    const modal = document.getElementById('hotel-modal');
    const modalImage = document.getElementById('hotel-modal-image');
    const modalTitle = document.getElementById('hotel-modal-title');
    const modalDescription = document.getElementById('hotel-modal-description');
    const modalPrice = document.getElementById('hotel-modal-price');
    const modalStars = document.getElementById('hotel-modal-stars');
    const amenitiesList = document.getElementById('amenities-list');

    modalImage.src = hotelData.imageUrl;
    modalImage.alt = hotelData.name;
    modalTitle.textContent = hotelData.name;
    modalDescription.textContent = hotelData.description;
    modalPrice.textContent = `$${hotelData.pricePerNight}/noche`;
    modalStars.innerHTML = generateStars(hotelData.stars);

    // Populate amenities
    amenitiesList.innerHTML = '';
    try {
        const amenities = JSON.parse(hotelData.amenities);
        amenities.forEach(amenity => {
            const li = document.createElement('li');
            li.textContent = amenity;
            amenitiesList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = hotelData.amenities;
        amenitiesList.appendChild(li);
    }

    modal.style.display = 'block';

    // Close modal functionality
    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

function viewVehicleDetails(vehicleId) {
    if (!vehiclesData) {
        alert('Vehicle data is still loading. Please wait a moment and try again.');
        return;
    }
    const vehicleData = vehiclesData.find(vehicle => vehicle.id === vehicleId);
    if (!vehicleData) {
        alert('Vehicle data not found for ID: ' + vehicleId);
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

    modalImage.src = vehicleData.imageUrl;
    modalImage.alt = vehicleData.name;
    modalTitle.textContent = vehicleData.name;
    modalDescription.textContent = vehicleData.description;
    modalPrice.textContent = `$${vehicleData.pricePerDay}/día`;
    vehicleCapacity.textContent = vehicleData.capacity;
    vehicleTransmission.textContent = vehicleData.transmission;
    vehicleFuel.textContent = vehicleData.fuelType;

    modal.style.display = 'block';

    // Close modal functionality
    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    // Close modal when clicking outside
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
        user: { id: user.id },
        destination: { id: parseInt(formData.get('destinationId')) },
        serviceType: 'destination',
        startDate: formData.get('checkInDate'),
        endDate: formData.get('checkOutDate'),
        numberOfPeople: parseInt(formData.get('numberOfGuests')),
        totalPrice: parseFloat(formData.get('totalPrice')),
        status: 'pending',
        specialRequests: formData.get('specialRequests') || ''
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
            alert('Reserva creada exitosamente');
            // Close the modal and redirect to reservations page
            document.getElementById('reservation-modal').style.display = 'none';
            window.location.href = 'reservations.html';
        } else {
            const error = await response.text();
            alert('Error al crear la reserva: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
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

    modalImage.src = packageData.imageUrl;
    modalImage.alt = `Paquete ${packageData.name}`;
    modalTitle.textContent = `Reservar Paquete ${packageData.name}`;
    modalDescription.textContent = packageData.description;
    modalPrice.textContent = `$${packageData.price}`;
    destinationIdInput.value = destinationId;
    totalPriceInput.value = packageData.price;

    // Populate includes
    includesList.innerHTML = '';
    try {
        const includes = JSON.parse(packageData.includes);
        includes.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            includesList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = packageData.includes;
        includesList.appendChild(li);
    }

    // Populate itinerary
    itineraryList.innerHTML = '';
    try {
        const itinerary = JSON.parse(packageData.itinerary);
        itinerary.forEach(item => {
            const li = document.createElement('li');
            li.textContent = item;
            itineraryList.appendChild(li);
        });
    } catch (e) {
        const li = document.createElement('li');
        li.textContent = packageData.itinerary;
        itineraryList.appendChild(li);
    }

    modal.style.display = 'block';

    // Close modal functionality
    const closeBtn = modal.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = 'none';
    };

    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
}

// Reservation Sidebar Functions
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
        // For now, we'll use a placeholder since flights don't have individual data
        item = {
            type: 'flight',
            id: id,
            name: 'Vuelo Reservado',
            description: 'Vuelo seleccionado',
            price: 150 // Placeholder price
        };
        price = 150;
    } else if (type === 'hotel') {
        const hotel = hotelsData.find(h => h.id === id);
        if (hotel) {
            item = {
                type: 'hotel',
                id: hotel.id,
                name: hotel.name,
                description: hotel.description,
                price: hotel.pricePerNight
            };
            price = hotel.pricePerNight;
        }
    } else if (type === 'vehicle') {
        const vehicle = vehiclesData.find(v => v.id === id);
        if (vehicle) {
            item = {
                type: 'vehicle',
                id: vehicle.id,
                name: vehicle.name,
                description: vehicle.description,
                price: vehicle.pricePerDay
            };
            price = vehicle.pricePerDay;
        }
    } else if (type === 'package') {
        const packageData = packagesData.find(p => p.id === id);
        if (packageData) {
            item = {
                type: 'package',
                id: packageData.id,
                name: `Paquete ${packageData.name}`,
                description: packageData.description,
                price: packageData.price
            };
            price = packageData.price;
        }
    }

    if (item) {
        // Check if item already exists
        const existingIndex = reservationItems.findIndex(r => r.type === type && r.id === id);
        if (existingIndex === -1) {
            reservationItems.push(item);
            updateReservationSidebar();
        } else {
            alert('Este servicio ya está en tu reserva.');
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

    reservationItemsContainer.innerHTML = '';

    let totalPrice = 0;

    // Separate packages from other services
    const packages = reservationItems.filter(item => item.type === 'package');
    const otherServices = reservationItems.filter(item => item.type !== 'package');

    // Add packages section if any
    if (packages.length > 0) {
        const packagesSection = document.createElement('div');
        packagesSection.className = 'reservation-section';
        packagesSection.innerHTML = '<h4>Paquetes</h4>';
        reservationItemsContainer.appendChild(packagesSection);

        packages.forEach((item, index) => {
            const itemElement = document.createElement('div');
            itemElement.className = 'reservation-item';
            itemElement.innerHTML = `
                <h4>${item.name}</h4>
                <p>${item.description}</p>
                <p class="price">$${item.price}</p>
                <button class="remove-item" onclick="removeFromReservation(${reservationItems.indexOf(item)})">×</button>
            `;
            packagesSection.appendChild(itemElement);
            totalPrice += item.price;
        });
    }

    // Add other services section if any
    if (otherServices.length > 0) {
        const servicesSection = document.createElement('div');
        servicesSection.className = 'reservation-section';
        servicesSection.innerHTML = '<h4>Reserva Personalizada</h4>';
        reservationItemsContainer.appendChild(servicesSection);

        otherServices.forEach((item, index) => {
            const itemElement = document.createElement('div');
            itemElement.className = 'reservation-item';
            itemElement.innerHTML = `
                <h4>${item.name}</h4>
                <p>${item.description}</p>
                <p class="price">$${item.price}</p>
                <button class="remove-item" onclick="removeFromReservation(${reservationItems.indexOf(item)})">×</button>
            `;
            servicesSection.appendChild(itemElement);
            totalPrice += item.price;
        });
    }

    totalPriceElement.textContent = totalPrice;

    // Update cart count
    if (cartCountElement) {
        cartCountElement.textContent = reservationItems.length;
    }

    // Enable/disable confirm button
    if (confirmBtn) {
        confirmBtn.disabled = reservationItems.length === 0;
    }
}

function clearReservation() {
    reservationItems = [];
    updateReservationSidebar();
}

async function confirmReservation() {
    if (reservationItems.length === 0) {
        alert('No hay servicios en la reserva.');
        return;
    }

    const user = JSON.parse(localStorage.getItem('user'));

    // Separate packages from other services
    const packages = reservationItems.filter(item => item.type === 'package');
    const otherServices = reservationItems.filter(item => item.type !== 'package');

    let successCount = 0;
    let errorMessages = [];

    // Handle packages separately (create individual reservations)
    for (const packageItem of packages) {
        const reservationData = {
            user: { id: user.id },
            destination: { id: packageItem.id },
            serviceType: 'destination',
            startDate: new Date().toISOString().split('T')[0], // Default to today
            endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // Default to 7 days later
            numberOfPeople: 2, // Default number of people
            totalPrice: packageItem.price,
            status: 'pending',
            specialRequests: `Paquete: ${packageItem.name}`
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
                successCount++;
            } else {
                const error = await response.text();
                errorMessages.push(`Error en paquete ${packageItem.name}: ${error}`);
            }
        } catch (error) {
            console.error('Error:', error);
            errorMessages.push(`Error de conexión en paquete ${packageItem.name}`);
        }
    }

    // Handle other services as combined reservation
    if (otherServices.length > 0) {
        const totalPrice = otherServices.reduce((sum, item) => sum + item.price, 0);
        const reservationData = {
            user: { id: user.id },
            serviceType: 'combined',
            totalPrice: totalPrice,
            status: 'pending',
            specialRequests: `Servicios incluidos: ${otherServices.map(item => item.name).join(', ')}`
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
                successCount++;
            } else {
                const error = await response.text();
                errorMessages.push(`Error en servicios combinados: ${error}`);
            }
        } catch (error) {
            console.error('Error:', error);
            errorMessages.push('Error de conexión en servicios combinados');
        }
    }

    // Show results
    if (successCount > 0) {
        alert(`Reserva(s) creada(s) exitosamente (${successCount} reserva(s))`);
        clearReservation();
        window.location.href = 'reservations.html';
    } else {
        alert('Error al crear las reservas: ' + errorMessages.join('; '));
    }
}

// Sidebar Toggle Functions
function initializeSidebarToggle() {
    const toggleBtn = document.getElementById('toggle-sidebar');
    const sidebar = document.getElementById('reservation-sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }
}

// Filter Functions
function populateFlightFilters(flights) {
    const originSelect = document.getElementById('flights-origin');
    const destinationSelect = document.getElementById('flights-destination');

    if (!originSelect || !destinationSelect) return;

    // Get unique origins and destinations
    const origins = [...new Set(flights.map(flight => flight.origin))];
    const destinations = [...new Set(flights.map(flight => flight.destination))];

    // Populate origins
    origins.forEach(origin => {
        const option = document.createElement('option');
        option.value = origin;
        option.textContent = origin;
        originSelect.appendChild(option);
    });

    // Populate destinations
    destinations.forEach(destination => {
        const option = document.createElement('option');
        option.value = destination;
        option.textContent = destination;
        destinationSelect.appendChild(option);
    });
}

function populateHotelDestinations(flights) {
    const hotelDestinationSelect = document.getElementById('hotels-destination');

    if (!hotelDestinationSelect) return;

    // Get unique destinations from flights
    const destinations = [...new Set(flights.map(flight => flight.destination))];

    // Populate hotel destinations
    destinations.forEach(destination => {
        const option = document.createElement('option');
        option.value = destination;
        option.textContent = destination;
        hotelDestinationSelect.appendChild(option);
    });
}

function populateVehicleTypes(vehicles) {
    const vehicleTypeSelect = document.getElementById('vehicles-type');

    if (!vehicleTypeSelect) return;

    // Get unique vehicle types
    const types = [...new Set(vehicles.map(vehicle => vehicle.type))];

    // Populate vehicle types
    types.forEach(type => {
        const option = document.createElement('option');
        option.value = type;
        option.textContent = type;
        vehicleTypeSelect.appendChild(option);
    });
}

// Filter event listeners
document.addEventListener('DOMContentLoaded', function() {
    // ... existing code ...

    // Add filter event listeners
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
        pkg.name.toLowerCase().includes(searchTerm) ||
        pkg.description.toLowerCase().includes(searchTerm)
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
        const matchesOrigin = !selectedOrigin || flight.origin === selectedOrigin;
        const matchesDestination = !selectedDestination || flight.destination === selectedDestination;
        return matchesOrigin && matchesDestination;
    });

    displayFlights(filteredFlights);
}

function filterHotels() {
    const nameInput = document.getElementById('hotels-name');
    const destinationSelect = document.getElementById('hotels-destination');

    if (!nameInput || !destinationSelect || !hotelsData) return;

    const searchTerm = nameInput.value.toLowerCase();
    const selectedDestination = destinationSelect.value;

    const filteredHotels = hotelsData.filter(hotel => {
        const matchesName = !searchTerm || hotel.name.toLowerCase().includes(searchTerm);
        const matchesDestination = !selectedDestination || hotel.destination === selectedDestination;
        return matchesName && matchesDestination;
    });

    displayHotels(filteredHotels);
}

function filterVehicles() {
    const typeSelect = document.getElementById('vehicles-type');

    if (!typeSelect || !vehiclesData) return;

    const selectedType = typeSelect.value;

    const filteredVehicles = vehiclesData.filter(vehicle =>
        !selectedType || vehicle.type === selectedType
    );

    displayVehicles(filteredVehicles);
}
