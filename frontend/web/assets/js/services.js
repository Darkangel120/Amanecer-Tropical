// Services JavaScript
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables for storing data
let packagesData, hotelsData, vehiclesData;

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

function displayPackages(destinations) {
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
            <img src="${destination.imageUrl}" alt="Paquete ${destination.name}">
            <h3>Paquete ${destination.name}</h3>
            <p>${destination.description}</p>
            <div class="service-price">$${destination.price}</div>
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
    // Redirect to booking page or open modal with destination details
    window.location.href = `services.html?destination=${destinationId}`;
}

async function loadFlights() {
    try {
        const response = await fetch(`${API_BASE_URL}/flights`);
        if (response.ok) {
            const flights = await response.json();
            displayFlights(flights);
        } else {
            console.error('Error loading flights');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayFlights(flights) {
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
        flightCard.innerHTML = `
            <h3>${flight.origin} - ${flight.destination}</h3>
            <p>${flight.description}</p>
            <div class="service-price">$${flight.price}</div>
            <a href="#" class="btn">Reservar Vuelo</a>
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

function displayHotels(hotels) {
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
            <div class="service-price">$${hotel.pricePerNight}/noche</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewHotelDetails(${hotel.id})">Ver Detalles</a>
                <a href="#" class="btn">Reservar Hotel</a>
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
        } else {
            console.error('Error loading vehicles');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayVehicles(vehicles) {
    const vehiclesGrid = document.getElementById('vehicles-grid');
    if (!vehiclesGrid) return;

    vehiclesGrid.innerHTML = '';

    vehicles.forEach(vehicle => {
        const vehicleCard = document.createElement('div');
        vehicleCard.className = 'service-card';
        vehicleCard.innerHTML = `
            <h3>${vehicle.name}</h3>
            <p>${vehicle.description}</p>
            <div class="service-price">$${vehicle.pricePerDay}/día</div>
            <div class="card-buttons">
                <a href="#" class="btn btn-details" onclick="viewVehicleDetails(${vehicle.id})">Ver Detalles</a>
                <a href="#" class="btn" style="font-size: 13px;">Alquilar Vehículo</a>
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
