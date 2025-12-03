package com.teamoffour.lms.service.strategy;

import com.teamoffour.lms.domain.BorrowingPolicy;
import org.springframework.stereotype.Service;




@Service("PREMIUM")
public class PremiumPlan implements IMembershipPlan{
    private static final BorrowingPolicy POLICY =
            new BorrowingPolicy(10, 0.25, 30);

    public PremiumPlan() {

    }

    @Override
    public Integer getMaxReservationsAllowed() {
        return 5;
    }

    @Override
    public BorrowingPolicy getBorrowingPolicy() {
        return POLICY;
    }

    @Override
    public String getPlanName() {
        return "Premium Plan";
    }

    @Override
    public Integer getCost() {
        return 7;
    }
}
