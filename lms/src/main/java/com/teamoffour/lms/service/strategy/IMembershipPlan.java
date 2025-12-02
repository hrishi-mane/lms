package com.teamoffour.lms.service.strategy;

import com.teamoffour.lms.domain.BorrowingPolicy;

public interface IMembershipPlan {
    Integer getMaxReservationsAllowed();
    BorrowingPolicy getBorrowingPolicy();
    String getPlanName();
    Integer getCost();
}
