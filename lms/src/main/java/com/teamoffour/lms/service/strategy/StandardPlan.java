package com.teamoffour.lms.service.strategy;

import com.teamoffour.lms.domain.BorrowingPolicy;
import org.springframework.stereotype.Service;


@Service("STANDARD")
public class StandardPlan implements IMembershipPlan {


    public StandardPlan() {
    }


    @Override
    public Integer getMaxReservationsAllowed() {
        return 2;
    }

    @Override
    public BorrowingPolicy getBorrowingPolicy() {
        return new BorrowingPolicy(10, 0.25, 30);
    }

    @Override
    public String getPlanName() {
        return "Standard Plan";
    }

    @Override
    public Integer getCost() {
        return 5;
    }
}
