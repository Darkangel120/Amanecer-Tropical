package com.AmanecerTropical.config;

import com.AmanecerTropical.entity.Review;
import com.AmanecerTropical.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("reviewSecurity")
public class ReviewSecurity {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserSecurity userSecurity;

    public boolean hasReviewAccess(Authentication authentication, Long reviewId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<Review> review = reviewService.getReviewById(reviewId);
        if (review.isPresent()) {
            Long userId = review.get().getUsuario().getId();
            return userSecurity.hasUserId(authentication, userId) || userSecurity.isAdmin(authentication);
        }
        return false;
    }
}