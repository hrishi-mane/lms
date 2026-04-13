package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.enums.PlanType;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import com.teamoffour.lms.service.strategy.PremiumPlan;
import com.teamoffour.lms.service.strategy.StandardPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MembershipPlanFactory {
    private static final Logger log = LoggerFactory.getLogger(MembershipPlanFactory.class);

    private final Map<PlanType, IMembershipPlan> planRegistry;

    public MembershipPlanFactory() {
        this.planRegistry = new HashMap<>();
        initializePlans();
        validatePlans();
    }

    private void initializePlans() {
        planRegistry.put(PlanType.STANDARD, new StandardPlan());
        planRegistry.put(PlanType.PREMIUM, new PremiumPlan());

        log.info("Initialized {} membership plans", planRegistry.size());
    }

    private void validatePlans() {
        for (PlanType type : PlanType.values()) {
            if (!planRegistry.containsKey(type)) {
                throw new IllegalStateException(
                        "Missing implementation for plan type: " + type +
                                ". Please add implementation in initializePlans()");
            }
        }
        log.info("✓ All plan types validated successfully");
    }

    public IMembershipPlan getPlan(PlanType type) {
        if (type == null) {
            throw new IllegalArgumentException("Plan type cannot be null");
        }

        IMembershipPlan plan = planRegistry.get(type);

        if (plan == null) {
            throw new IllegalArgumentException(
                    String.format("Plan not found for type: %s. Available types: %s",
                            type, planRegistry.keySet()));
        }

        log.debug("Retrieved plan: {} ({})", plan.getPlanName(), type);
        return plan;
    }

    public PlanType[] getAvailablePlanTypes() {
        return planRegistry.keySet().toArray(new PlanType[0]);
    }

    public boolean isPlanTypeSupported(PlanType type) {
        return type != null && planRegistry.containsKey(type);
    }

    public int getAvailablePlansCount() {
        return planRegistry.size();
    }

    public Map<PlanType, String> getAllPlanInfo() {
        Map<PlanType, String> planInfo = new HashMap<>();

        planRegistry.forEach((type, plan) ->
                planInfo.put(type, String.format("%s ($%d)",
                        plan.getPlanName(),
                        plan.getCost()))
        );

        return planInfo;
    }
}