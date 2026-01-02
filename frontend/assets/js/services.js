// Services JavaScript
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

    destinations.forEach(destination => {
        const packageCard = document.createElement('div');
        packageCard.className = 'service-card';
        packageCard.innerHTML = `
            <img src="${destination.imageUrl}" alt="Paquete ${destination.name}">
            <h3>Paquete ${destination.name}</h3>
            <p>${destination.description}</p>
            <div class="service-price">$${destination.price}</div>
            <a href="#" class="btn" onclick="bookPackage(${destination.id})">Reservar Ahora</a>
        `;
        packagesGrid.appendChild(packageCard);
    });
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

    hotels.forEach(hotel => {
        const hotelCard = document.createElement('div');
        hotelCard.className = 'service-card';
        hotelCard.innerHTML = `
            <img src="${hotel.imageUrl}" alt="${hotel.name}">
            <h3>${hotel.name}</h3>
            <p>${hotel.description}</p>
            <div class="service-price">$${hotel.price}/noche</div>
            <a href="#" class="btn">Reservar Hotel</a>
        `;
        hotelsGrid.appendChild(hotelCard);
    });
}

async function loadVehicles() {
    try {
        const response = await fetch(`${API_BASE_URL}/vehicles`);
        if (response.ok) {
            const vehicles = await response.json();
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
            <div class="service-price">$${vehicle.price}/día</div>
            <a href="#" class="btn">Alquilar Vehículo</a>
        `;
        vehiclesGrid.appendChild(vehicleCard);
    });
}

async function createReservation() {
    const formData = new FormData(document.querySelector('#booking-form'));
    const user = JSON.parse(localStorage.getItem('user'));

    const reservationData = {
        userId: user.id,
        destinationId: parseInt(formData.get('destinationId')),
        checkInDate: formData.get('checkInDate'),
        checkOutDate: formData.get('checkOutDate'),
        numberOfGuests: parseInt(formData.get('numberOfGuests')),
        totalPrice: parseFloat(formData.get('totalPrice')),
        status: 'PENDING'
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
