package com.teamoffour.lms.service.decorator;

import com.teamoffour.lms.domain.BorrowingPolicy;
import com.teamoffour.lms.service.strategy.IMembershipPlan;

public abstract class MembershipDecorator implements IMembershipPlan {
    protected IMembershipPlan wrapped;

    public MembershipDecorator(IMembershipPlan wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Integer getMaxReservationsAllowed() {
        return wrapped.getMaxReservationsAllowed();
    }

    @Override
    public BorrowingPolicy getBorrowingPolicy() {
        return wrapped.getBorrowingPolicy();
    }

    @Override
    public String getPlanName() {
        return wrapped.getPlanName();
    }

    @Override
    public Integer getCost() {
        return wrapped.getCost();
    }
}
