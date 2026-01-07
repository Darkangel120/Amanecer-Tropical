// Update notification badge count
function updateNotificationBadge() {
    const unreadCount = document.querySelectorAll('.notification-item.unread').length;
    const badge = document.querySelector('.notification-badge');
    if (badge) {
        badge.textContent = unreadCount;
        badge.style.display = unreadCount > 0 ? 'block' : 'none';
    }
}

// General JavaScript for Shared User Menu Functionality
document.addEventListener('DOMContentLoaded', function() {
    // Create user menu dynamically
    createUserMenu();
    // Load user information for profile dropdown
    loadUserProfileInfo();
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
    loadNotifications();

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
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
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

    // Logout functionality (for pages that still have logout buttons)
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
                if (freshUserData.profilePicture && freshUserData.profilePicture.startsWith('/uploads/')) {
                    // Profile picture is a file path from backend
                    profileAvatar.src = `http://localhost:8080${freshUserData.profilePicture}`;
                    profileAvatar.onerror = function() {
                        // If profile picture fails to load, show default
                        profileAvatar.src = '../assets/img/default-profile.png';
                    };
                } else if (freshUserData.profilePicture && freshUserData.profilePicture.startsWith('data:image')) {
                    // Profile picture is base64 (fallback for old data)
                    profileAvatar.src = freshUserData.profilePicture;
                } else {
                    // No profile picture, show default
                    profileAvatar.src = '../assets/img/default-profile.png';
                }
            }

            // Update profile menu information
            const profileAvatarLarge = document.querySelector('.profile-avatar-large');
            if (profileAvatarLarge) {
                if (freshUserData.profilePicture && freshUserData.profilePicture.startsWith('/uploads/')) {
                    // Profile picture is a file path from backend
                    profileAvatarLarge.src = `http://localhost:8080${freshUserData.profilePicture}`;
                    profileAvatarLarge.onerror = function() {
                        // If profile picture fails to load, show default
                        profileAvatarLarge.src = '../assets/img/default-profile.png';
                    };
                } else if (freshUserData.profilePicture && freshUserData.profilePicture.startsWith('data:image')) {
                    // Profile picture is base64 (fallback for old data)
                    profileAvatarLarge.src = freshUserData.profilePicture;
                } else {
                    // No profile picture, show default
                    profileAvatarLarge.src = '../assets/img/default-profile.png';
                }
            }

            const profileName = document.querySelector('.profile-details h4');
            if (profileName && freshUserData.name) {
                profileName.textContent = freshUserData.name;
            }

            const profileEmail = document.querySelector('.profile-details p');
            if (profileEmail && freshUserData.email) {
                profileEmail.textContent = freshUserData.email;
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
        notificationItem.className = `notification-item ${notification.read ? '' : 'unread'}`;
        notificationItem.dataset.id = notification.id;
        notificationItem.innerHTML = `
            <i class="fas fa-${getNotificationIcon(notification.type)}"></i>
            <div class="notification-content">
                <p>${notification.message}</p>
                <span class="notification-time">${new Date(notification.createdAt).toLocaleString()}</span>
            </div>
        `;

        // Add click event to mark as read
        if (!notification.read) {
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
                'Authorization': `Bearer ${localStorage.getItem('token')}`
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
        case 'reservation':
            return 'calendar-check';
        case 'payment':
            return 'credit-card';
        case 'review':
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
                    <img src="../assets/img/default-avatar.png" alt="Perfil" class="profile-avatar">
                </button>
                <div class="profile-menu" id="profileMenu">
                    <div class="profile-info">
                        <img src="../assets/img/default-avatar.png" alt="Perfil" class="profile-avatar-large">
                        <div class="profile-details">
                            <h4></h4>
                            <p></p>
                        </div>
                    </div>
                    <div class="profile-options">
                        <a href="profile.html" class="profile-option">
                            <i class="fas fa-user"></i>
                            <span>Mi Perfil</span>
                        </a>
                        <a href="../index.html" class="profile-option logout-option">
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
}

// Fallback function to load from localStorage
function updateUIFromLocalStorage() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;

    // Update profile button avatar
    const profileAvatar = document.querySelector('.profile-avatar');
    if (profileAvatar) {
        if (user.profilePicture && user.profilePicture.startsWith('/uploads/')) {
            // Profile picture is a file path from backend
            profileAvatar.src = `http://localhost:8080${user.profilePicture}`;
            profileAvatar.onerror = function() {
                // If profile picture fails to load, show default
                profileAvatar.src = '../assets/img/default-profile.png';
            };
        } else if (user.profilePicture && user.profilePicture.startsWith('data:image')) {
            // Profile picture is base64 (fallback for old data)
            profileAvatar.src = user.profilePicture;
        } else {
            // No profile picture, show default
            profileAvatar.src = '../assets/img/default-profile.png';
        }
    }

    // Update profile menu information
    const profileAvatarLarge = document.querySelector('.profile-avatar-large');
    if (profileAvatarLarge) {
        if (user.profilePicture && user.profilePicture.startsWith('/uploads/')) {
            // Profile picture is a file path from backend
            profileAvatarLarge.src = `http://localhost:8080${user.profilePicture}`;
            profileAvatarLarge.onerror = function() {
                // If profile picture fails to load, show default
                profileAvatarLarge.src = '../assets/img/default-profile.png';
            };
        } else if (user.profilePicture && user.profilePicture.startsWith('data:image')) {
            // Profile picture is base64 (fallback for old data)
            profileAvatarLarge.src = user.profilePicture;
        } else {
            // No profile picture, show default
            profileAvatarLarge.src = '../assets/img/default-profile.png';
        }
    }

    const profileName = document.querySelector('.profile-details h4');
    if (profileName && user.name) {
        profileName.textContent = user.name;
    }

    const profileEmail = document.querySelector('.profile-details p');
    if (profileEmail && user.email) {
        profileEmail.textContent = user.email;
    }
}
