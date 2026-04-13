package com.teamoffour.lms.service.decorator;

import com.teamoffour.lms.domain.BorrowingPolicy;
import com.teamoffour.lms.service.strategy.IMembershipPlan;

public class ExtendedBorrowingDecorator extends MembershipDecorator {

    public ExtendedBorrowingDecorator(IMembershipPlan wrapped) {
        super(wrapped);
    }

    @Override
    public BorrowingPolicy getBorrowingPolicy() {
        BorrowingPolicy base = wrapped.getBorrowingPolicy();
        return new BorrowingPolicy(
                base.getBorrowingLimit() + 2,
                base.getFineRatePerDay(),
                base.getBorrowingDurationInDays() + 7
        );
    }

    @Override
    public Integer getCost() {
        return wrapped.getCost() + 3;
    }
}

