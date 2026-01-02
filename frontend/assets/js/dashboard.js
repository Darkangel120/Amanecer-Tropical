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

    // Load destinations
    loadDestinations();

    // Load user info
    loadUserInfo();

    // Load user stats
    loadUserStats();
});

async function loadDestinations() {
    try {
        const response = await fetch(`${API_BASE_URL}/destinations`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const destinations = await response.json();
            displayDestinations(destinations);
        } else {
            console.error('Error loading destinations');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayDestinations(destinations) {
    const destinationsContainer = document.querySelector('.destinations-grid');
    if (!destinationsContainer) return;

    destinationsContainer.innerHTML = '';

    destinations.forEach(destination => {
        // Parse includes and itinerary if they are strings
        let includes = destination.includes;
        let itinerary = destination.itinerary;
        if (typeof includes === 'string') {
            try {
                includes = JSON.parse(includes);
            } catch (e) {
                includes = [];
            }
        }
        if (typeof itinerary === 'string') {
            try {
                itinerary = JSON.parse(itinerary);
            } catch (e) {
                itinerary = [];
            }
        }

        const destinationCard = document.createElement('div');
        destinationCard.className = 'destination-card';
        destinationCard.innerHTML = `
            <img src="${destination.imageUrl || '../assets/img/default-destination.jpg'}" alt="${destination.name}">
            <div class="destination-info">
                <h3>${destination.name}</h3>
                <p class="location"><strong>Ubicación:</strong> ${destination.location}</p>
                <p class="category"><strong>Categoría:</strong> ${destination.category}</p>
                <p class="duration"><strong>Duración:</strong> ${destination.durationDays} días</p>
                <p class="description"><strong>Descripción:</strong> ${destination.description}</p>
                <p class="price"><strong>Precio:</strong> $${destination.price}</p>
                <div class="includes">
                    <strong>Incluye:</strong>
                    <ul>
                        ${includes.map(item => `<li>${item}</li>`).join('')}
                    </ul>
                </div>
                <div class="itinerary">
                    <strong>Itinerario:</strong>
                    <ol>
                        ${itinerary.map(day => `<li>${day}</li>`).join('')}
                    </ol>
                </div>
                <button class="btn-book" data-id="${destination.id}">Reservar</button>
            </div>
        `;
        destinationsContainer.appendChild(destinationCard);
    });

    // Add event listeners for book buttons
    document.querySelectorAll('.btn-book').forEach(btn => {
        btn.addEventListener('click', function() {
            const destinationId = this.getAttribute('data-id');
            window.location.href = `services.html?destination=${destinationId}`;
        });
    });
}

async function loadUserInfo() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user) {
        const userNameElement = document.querySelector('.user-name');
        if (userNameElement) {
            userNameElement.textContent = user.name;
        }
    }
}

async function loadUserStats() {
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

            // Count active reservations (pending or confirmed)
            const activeReservations = reservations.filter(r => r.status === 'pending' || r.status === 'confirmed').length;

            // Find next trip (earliest start date)
            const upcomingReservations = reservations.filter(r => new Date(r.startDate) > new Date());
            let nextTrip = 'Ninguno';
            if (upcomingReservations.length > 0) {
                const nextReservation = upcomingReservations.reduce((earliest, current) =>
                    new Date(current.startDate) < new Date(earliest.startDate) ? current : earliest
                );
                nextTrip = new Date(nextReservation.startDate).toLocaleDateString('es-ES');
            }

            // Calculate total spent (confirmed reservations)
            const totalSpent = reservations
                .filter(r => r.status === 'confirmed')
                .reduce((sum, r) => sum + parseFloat(r.totalPrice), 0);

            // Update the HTML elements
            const statCards = document.querySelectorAll('.stat-card');

            // Active reservations (first stat card)
            const activeReservationsElement = statCards[0].querySelector('h3');
            if (activeReservationsElement) {
                activeReservationsElement.textContent = activeReservations;
            }

            // Next trip (second stat card)
            const nextTripElement = statCards[1].querySelector('p');
            if (nextTripElement) {
                nextTripElement.textContent = nextTrip;
            }

            // Total spent (third stat card)
            const totalSpentElement = statCards[2].querySelector('h3');
            if (totalSpentElement) {
                totalSpentElement.textContent = `$${totalSpent.toFixed(2)}`;
            }
        } else {
            console.error('Error loading user stats');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
