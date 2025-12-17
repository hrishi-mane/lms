package com.teamoffour.lms.service.dto;

import com.teamoffour.lms.domain.enums.AddOns;
import com.teamoffour.lms.domain.enums.PlanType;
import lombok.Data;

@Data
public class RegisterMemberRequest {
    private String userName;
    private String password;
    private String emailId;
    private String phoneNumber;
    private PlanType planType;
    private Integer memberShipMonths;
    private AddOns addOns;
}