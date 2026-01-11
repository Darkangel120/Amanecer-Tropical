const basePath = getBasePath();

function updateNotificationBadge() {
    const unreadCount = document.querySelectorAll('.notification-item.unread').length;
    const badge = document.querySelector('.notification-badge');
    if (badge) {
        badge.textContent = unreadCount;
        badge.style.display = unreadCount > 0 ? 'block' : 'none';
    }
}

// Function to validate session with backend
async function validateSession() {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    // If no token or user in localStorage, redirect immediately
    if (!token || !user) {
        console.log('No token or user found, redirecting to login');
        window.location.href = '../index.html';
        return false;
    }

    try {
        // Make API call to validate token with backend
        const response = await fetch('http://localhost:8080/api/auth/validate', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            // Token is valid, session is active
            console.log('Session validated successfully');
            return true;
        } else if (response.status === 401) {
            // Token is invalid or expired
            console.log('Token invalid or expired, clearing session and redirecting');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../index.html';
            return false;
        } else {
            // Other server error, but don't redirect immediately
            console.warn('Server error during session validation, but keeping session active');
            return true;
        }
    } catch (error) {
        // Network error or server unreachable
        console.warn('Network error during session validation, but keeping session active:', error);
        return true; // Don't redirect on network errors
    }
}

// General JavaScript for Shared User Menu Functionality
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    // Only create user menu if user is logged in
    if (user) {
        createUserMenu();
        loadUserProfileInfo();
        // Hide login/register buttons when user is logged in
        const authButtons = document.querySelector('.auth-buttons');
        if (authButtons) {
            authButtons.style.display = 'none';
        }
        // Add Dashboard link to main navigation
        addDashboardToNav();
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

    // Profile dropdown functionality
    const profileBtn = document.getElementById('profileBtn');
    const profileMenu = document.getElementById('profileMenu');

    if (profileBtn && profileMenu) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.classList.toggle('active');
            // Close notifications menu if open
            const notificationsMenu = document.getElementById('notificationsMenu');
            if (notificationsMenu && notificationsMenu.classList.contains('active')) {
                notificationsMenu.classList.remove('active');
            }
        });
    }

    // Notifications dropdown functionality
    const notificationsBtn = document.getElementById('notificationsBtn');
    const notificationsMenu = document.getElementById('notificationsMenu');

    if (notificationsBtn && notificationsMenu) {
        notificationsBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            notificationsMenu.classList.toggle('active');
            // Close profile menu if open
            if (profileMenu && profileMenu.classList.contains('active')) {
                profileMenu.classList.remove('active');
            }
            // Load notifications if menu is opening
            if (notificationsMenu.classList.contains('active')) {
                loadNotifications();
            }
        });
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', () => {
        if (profileMenu) profileMenu.classList.remove('active');
        if (notificationsMenu) notificationsMenu.classList.remove('active');
    });

    // Prevent closing when clicking inside dropdowns
    if (profileMenu) {
        profileMenu.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }

    if (notificationsMenu) {
        notificationsMenu.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }

    // Load notifications from backend
    if (user) {
        loadNotifications();
    }

    // Mark all notifications as read
    const markAllReadBtn = document.querySelector('.mark-all-read');
    if (markAllReadBtn) {
        markAllReadBtn.addEventListener('click', async () => {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;

            try {
                const response = await fetch('http://localhost:8080/api/notifications/mark-all-read', {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    const unreadNotifications = document.querySelectorAll('.notification-item.unread');
                    unreadNotifications.forEach(item => {
                        item.classList.remove('unread');
                    });
                    // Update badge count
                    updateNotificationBadge();
                }
            } catch (error) {
                console.error('Error marking notifications as read:', error);
            }
        });
    }

    // Initialize badge on page load
    updateNotificationBadge();

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

    // Add hover effects to section cards (for pages that have them)
    const sectionCards = document.querySelectorAll('.section-card');
    sectionCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
});

// Function to load user profile information from backend
async function loadUserProfileInfo() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    try {
        const response = await fetch(`http://localhost:8080/api/users/${user.id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const freshUserData = await response.json();
            // Update localStorage with fresh data
            localStorage.setItem('user', JSON.stringify(freshUserData));

            // Update profile button avatar
            const profileAvatar = document.querySelector('.profile-avatar');
            if (profileAvatar) {
                if (freshUserData.fotoPerfil && freshUserData.fotoPerfil.startsWith('/uploads/')) {
                    // Profile picture is a file path from backend
                    profileAvatar.src = `http://localhost:8080${freshUserData.fotoPerfil}`;
                    profileAvatar.onerror = function() {
                        // If profile picture fails to load, show default
                        profileAvatar.src = '../assets/img/default-profile.png';
                    };
                } else if (freshUserData.fotoPerfil && freshUserData.fotoPerfil.startsWith('data:image')) {
                    // Profile picture is base64 (fallback for old data)
                    profileAvatar.src = freshUserData.fotoPerfil;
                } else {
                    // No profile picture, show default
                    profileAvatar.src = '../assets/img/default-profile.png';
                }
            }

            // Update profile menu information
            const profileAvatarLarge = document.querySelector('.profile-avatar-large');
            if (profileAvatarLarge) {
                if (freshUserData.fotoPerfil && freshUserData.fotoPerfil.startsWith('/uploads/')) {
                    // Profile picture is a file path from backend
                    profileAvatarLarge.src = `http://localhost:8080${freshUserData.fotoPerfil}`;
                    profileAvatarLarge.onerror = function() {
                        // If profile picture fails to load, show default
                        profileAvatarLarge.src = '../assets/img/default-profile.png';
                    };
                } else if (freshUserData.fotoPerfil && freshUserData.fotoPerfil.startsWith('data:image')) {
                    // Profile picture is base64 (fallback for old data)
                    profileAvatarLarge.src = freshUserData.fotoPerfil;
                } else {
                    // No profile picture, show default
                    profileAvatarLarge.src = '../assets/img/default-profile.png';
                }
            }

            const profileName = document.querySelector('.profile-details h4');
            if (profileName && freshUserData.nombre) {
                profileName.textContent = freshUserData.nombre;
            }

            const profileEmail = document.querySelector('.profile-details p');
            if (profileEmail && freshUserData.correoElectronico) {
                profileEmail.textContent = freshUserData.correoElectronico;
            }
        } else {
            console.error('Error loading fresh user data, using localStorage');
            // Fallback to localStorage data
            updateUIFromLocalStorage();
        }
    } catch (error) {
        console.error('Error:', error);
        // Fallback to localStorage data
        updateUIFromLocalStorage();
    }
}

// Function to load notifications from backend
async function loadNotifications() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    try {
        const response = await fetch(`http://localhost:8080/api/notifications/user/${user.id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const notifications = await response.json();
            displayNotifications(notifications);
        } else {
            console.error('Error loading notifications');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Function to display notifications in the dropdown
function displayNotifications(notifications) {
    const notificationsList = document.querySelector('.notifications-list');
    if (!notificationsList) return;

    notificationsList.innerHTML = '';

    if (notifications.length === 0) {
        notificationsList.innerHTML = '<p>No tienes notificaciones.</p>';
        return;
    }

    notifications.forEach(notification => {
        const notificationItem = document.createElement('div');
        notificationItem.className = `notification-item ${notification.leido ? '' : 'unread'}`;
        notificationItem.dataset.id = notification.id;
        notificationItem.innerHTML = `
            <i class="fas fa-${getNotificationIcon(notification.tipo)}"></i>
            <div class="notification-content">
                <p>${notification.mensaje}</p>
                <span class="notification-time">${new Date(notification.fechaCreacion).toLocaleString()}</span>
            </div>
        `;

        // Add click event to mark as read
        if (!notification.leido) {
            notificationItem.addEventListener('click', async () => {
                await markNotificationAsRead(notification.id);
                notificationItem.classList.remove('unread');
                updateNotificationBadge();
            });
        }

        notificationsList.appendChild(notificationItem);
    });

    // Update badge count
    updateNotificationBadge();
}

// Function to mark a single notification as read
async function markNotificationAsRead(notificationId) {
    try {
        const response = await fetch(`http://localhost:8080/api/notifications/${notificationId}/read`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('Error marking notification as read');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Helper function to get notification icon based on type
function getNotificationIcon(type) {
    switch (type) {
        case 'reservacion':
            return 'calendar-check';
        case 'pago':
            return 'credit-card';
        case 'resena':
            return 'star';
        default:
            return 'bell';
    }
}

// Function to create user menu dynamically
function createUserMenu() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    const navLinks = document.querySelector('.nav-links');
    if (!navLinks) return;

    // Check if user menu already exists
    if (document.querySelector('.user-menu')) {
        return;
    }

    // Create user menu HTML
    const userMenuHTML = `
        <div class="user-menu">
            <!-- Notifications Dropdown -->
            <div class="notifications-dropdown">
                <button class="notifications-btn" id="notificationsBtn">
                    <i class="fas fa-bell"></i>
                    <span class="notification-badge">0</span>
                </button>
                <div class="notifications-menu" id="notificationsMenu">
                    <div class="notifications-header">
                        <h4>Notificaciones</h4>
                        <button class="mark-all-read">Marcar todas como leídas</button>
                    </div>
                    <div class="notifications-list">
                        <!-- Notifications will be loaded dynamically -->
                    </div>
                </div>
            </div>

            <!-- Profile Dropdown -->
            <div class="profile-dropdown">
                <button class="profile-btn" id="profileBtn">
                    <img src="../assets/img/default-profile.png" alt="Perfil" class="profile-avatar">
                </button>
                <div class="profile-menu" id="profileMenu">
                    <div class="profile-info">
                        <img src="../assets/img/default-profile.png" alt="Perfil" class="profile-avatar-large">
                        <div class="profile-details">
                            <h4></h4>
                            <p></p>
                        </div>
                    </div>
                    <div class="profile-options">
                        <a href="${basePath}user/profile.html" class="profile-option">
                            <i class="fas fa-user"></i>
                            <span>Mi Perfil</span>
                        </a>
                        <a href="#" class="profile-option logout-option">
                            <i class="fas fa-sign-out-alt"></i>
                            <span>Cerrar Sesión</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Insert user menu after nav-links
    navLinks.insertAdjacentHTML('afterend', userMenuHTML);

    // Add logout functionality to the newly created logout option
    setTimeout(() => {
        const logoutOption = document.querySelector('.logout-option');
        if (logoutOption) {
            logoutOption.addEventListener('click', function(e) {
                e.preventDefault();
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '../index.html';
            });
        }
    }, 100);
}

// Fallback function to load from localStorage
function updateUIFromLocalStorage() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    // Update profile button avatar
    const profileAvatar = document.querySelector('.profile-avatar');
    if (profileAvatar) {
        if (user.fotoPerfil && user.fotoPerfil.startsWith('/uploads/')) {
            // Profile picture is a file path from backend
            profileAvatar.src = `http://localhost:8080${user.fotoPerfil}`;
            profileAvatar.onerror = function() {
                // If profile picture fails to load, show default
                profileAvatar.src = '../assets/img/default-profile.png';
            };
        } else if (user.fotoPerfil && user.fotoPerfil.startsWith('data:image')) {
            // Profile picture is base64 (fallback for old data)
            profileAvatar.src = user.fotoPerfil;
        } else {
            // No profile picture, show default
            profileAvatar.src = '../assets/img/default-profile.png';
        }
    }

    // Update profile menu information
    const profileAvatarLarge = document.querySelector('.profile-avatar-large');
    if (profileAvatarLarge) {
        if (user.fotoPerfil && user.fotoPerfil.startsWith('/uploads/')) {
            // Profile picture is a file path from backend
            profileAvatarLarge.src = `http://localhost:8080${user.fotoPerfil}`;
            profileAvatarLarge.onerror = function() {
                // If profile picture fails to load, show default
                profileAvatarLarge.src = '../assets/img/default-profile.png';
            };
        } else if (user.fotoPerfil && user.fotoPerfil.startsWith('data:image')) {
            // Profile picture is base64 (fallback for old data)
            profileAvatarLarge.src = user.fotoPerfil;
        } else {
            // No profile picture, show default
            profileAvatarLarge.src = '../assets/img/default-profile.png';
        }
    }

    const profileName = document.querySelector('.profile-details h4');
    if (profileName && user.nombre) {
        profileName.textContent = user.nombre;
    }

    const profileEmail = document.querySelector('.profile-details p');
    if (profileEmail && user.correoElectronico) {
        profileEmail.textContent = user.correoElectronico;
    }
}

function getBasePath() {
    const pathParts = window.location.pathname.split('/').filter(part => part.length > 0);
    const depth = pathParts.length - 3;
    const base = depth > 0 ? '../'.repeat(depth) : '';
    return base;
}

// Function to add Dashboard link to main navigation
function addDashboardToNav() {
    const navLinks = document.querySelector('.nav-links');
    if (!navLinks) return;

    // Check if Dashboard link already exists
    if (document.querySelector('.nav-dashboard')) {
        return;
    }

    // Create Dashboard link
    const dashboardLink = document.createElement('li');
    dashboardLink.className = 'nav-dashboard';
    dashboardLink.innerHTML = `<a href="${basePath}user/dashboard.html">Dashboard</a>`;

    // Insert Dashboard link after the first navigation item (Inicio)
    const firstNavItem = navLinks.querySelector('li');
    if (firstNavItem) {
        firstNavItem.insertAdjacentElement('afterend', dashboardLink);
    } else {
        // If no first item, append to nav-links
        navLinks.appendChild(dashboardLink);
    }
}
