package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.enums.PlanType;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MembershipPlanFactory {

    private final Map<String, IMembershipPlan> plans;

    public MembershipPlanFactory(Map<String, IMembershipPlan> plans) {
        this.plans = plans;  // Spring injects (STANDARD → StandardPlan, PREMIUM → PremiumPlan)
    }

    public IMembershipPlan getPlan(PlanType type) {
        return plans.get(type.name());
    }
}
