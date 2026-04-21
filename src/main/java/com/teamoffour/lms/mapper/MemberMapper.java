package com.teamoffour.lms.mapper;

import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.service.dto.MemberDTO;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MemberMapper {

    public MemberDTO toMemberDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setUserName(member.getUserName());
        dto.setEmailId(member.getEmailId());
        dto.setPhoneNumber(member.getPhoneNumber());
        dto.setMembershipStartDate(member.getMembershipStartDate());
        dto.setMembershipEndDate(member.getMembershipEndDate());
        dto.setPlanName(member.getMembershipPlan().getPlanName());
        dto.setActiveBorrowCount(member.getActiveBorrowCount());
        dto.setActiveReservationCount(member.getActiveReservationCount());
        dto.setQueuedReservationCount(member.getQueuedReservationCount());
        return dto;
    }

    public Member convertAddMemberRequestToMember(RegisterMemberRequest registerMemberRequest, IMembershipPlan plan) {
        Member member = new Member();

        member.setUserName(registerMemberRequest.getUserName());
        member.setPassword(registerMemberRequest.getPassword());
        member.setEmailId(registerMemberRequest.getEmailId());
        member.setPhoneNumber(registerMemberRequest.getPhoneNumber());
        LocalDate start = LocalDate.now();
        member.setMembershipStartDate(start);
        member.setMembershipEndDate(start.plusMonths(registerMemberRequest.getMemberShipMonths()));
        member.setMembershipPlan(plan);

        return member;

    }
}
