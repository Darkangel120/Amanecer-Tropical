const navSlide = () => {
    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    burger.addEventListener('click', () => {
        nav.classList.toggle('nav-active');

        navLinks.forEach((link, index) => {
            if (link.style.animation) {
                link.style.animation = '';
            } else {
                link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
            }
        });

        burger.classList.toggle('toggle');
    });
}

const carousel = () => {
    const slides = document.querySelectorAll('.carousel-slide');
    const indicators = document.querySelectorAll('.indicator');
    const tooltip = document.querySelector('.carousel-tooltip');
    let currentSlide = 0;
    let autoSlideInterval;
    let isAutoSliding = true;
    let isTooltipVisible = false;

    const showTooltip = (slide) => {
        const tooltipText = slide.getAttribute('data-tooltip');
        if (tooltip && tooltipText) {
            tooltip.textContent = tooltipText;
            tooltip.style.opacity = '1';
            isTooltipVisible = true;
        }
    }

    const hideTooltip = () => {
        if (tooltip) {
            tooltip.style.opacity = '0';
            isTooltipVisible = false;
        }
    }

    const showSlide = (index) => {
        slides.forEach(slide => slide.classList.remove('active'));
        indicators.forEach(indicator => indicator.classList.remove('active'));

        slides[index].classList.add('active');
        indicators[index].classList.add('active');

        // Update tooltip if visible
        if (isTooltipVisible) {
            showTooltip(slides[index]);
        }

        currentSlide = index;
    }

    const nextSlide = () => {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    const startAutoSlide = () => {
        if (!isAutoSliding) {
            autoSlideInterval = setInterval(nextSlide, 4000);
            isAutoSliding = true;
        }
    }

    const stopAutoSlide = () => {
        if (isAutoSliding) {
            clearInterval(autoSlideInterval);
            isAutoSliding = false;
        }
    }

    // Event listeners for slides (hover and touch)
    slides.forEach((slide, index) => {
        // Desktop hover
        slide.addEventListener('mouseenter', () => {
            showTooltip(slide);
            stopAutoSlide();
        });

        slide.addEventListener('mouseleave', () => {
            hideTooltip();
            setTimeout(() => startAutoSlide(), 2000);
        });

        // Mobile touch
        slide.addEventListener('touchstart', (e) => {
            e.preventDefault();
            showTooltip(slide);
            isTooltipVisible = true;
        });
    });

    // Event listeners for indicators
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => {
            showSlide(index);
            stopAutoSlide();
            setTimeout(() => startAutoSlide(), 2000);
        });
    });

    // Start auto slide
    startAutoSlide();
}

const handleMobileButtons = () => {
    const loginBtn = document.querySelector('.btn-login');
    const registerBtn = document.querySelector('.btn-register');

    const showTooltip = (button) => {
        button.classList.add('show-tooltip');
        setTimeout(() => {
            button.classList.remove('show-tooltip');
        }, 2000);
    };

    loginBtn.addEventListener('click', (e) => {
        e.preventDefault();
        showTooltip(loginBtn);
    });

    registerBtn.addEventListener('click', (e) => {
        e.preventDefault();
        showTooltip(registerBtn);
    });

    // Touch events for mobile
    loginBtn.addEventListener('touchstart', (e) => {
        e.preventDefault();
        showTooltip(loginBtn);
    });

    registerBtn.addEventListener('touchstart', (e) => {
        e.preventDefault();
        showTooltip(registerBtn);
    });
};

navSlide();
carousel();
handleMobileButtons();
