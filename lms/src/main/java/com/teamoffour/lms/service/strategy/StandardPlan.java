package com.teamoffour.lms.service.strategy;

import com.teamoffour.lms.domain.BorrowingPolicy;
import org.springframework.stereotype.Service;


@Service("standardPlan")
public class StandardPlan implements IMembershipPlan {

    private static final BorrowingPolicy POLICY =
            new BorrowingPolicy(5, 0.25, 30);



    @Override
    public Integer getMaxReservationsAllowed() {
        return 2;
    }

    @Override
    public BorrowingPolicy getBorrowingPolicy() {
        return POLICY;
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
