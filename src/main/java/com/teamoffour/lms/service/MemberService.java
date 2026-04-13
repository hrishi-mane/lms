package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.mapper.MemberMapper;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.service.decorator.ExtendedBorrowingDecorator;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService implements MemberInterface {
    private final MemberRepository memberRepository;
    private final MembershipPlanFactory membershipPlanFactory;
    private final MemberMapper memberMapper;

    @Autowired
    public MemberService(MemberRepository memberRepository, MembershipPlanFactory membershipPlanFactory, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.membershipPlanFactory = membershipPlanFactory;
        this.memberMapper = memberMapper;
    }

    @Override
    public String registerMember(RegisterMemberRequest registerMemberRequest) {
        IMembershipPlan selectedPlan = membershipPlanFactory.getPlan(registerMemberRequest.getPlanType());

        //Add the Extended Borrowing AddOn as a decorator to the selected plan. New Decorators can be added in future as new addOns.
        if (registerMemberRequest.getAddOns() != null) {
            selectedPlan = new ExtendedBorrowingDecorator(selectedPlan);
        }

        Member member = memberMapper.convertAddMemberRequestToMember(registerMemberRequest, selectedPlan);

        return "Member created with Id : " + memberRepository.save(member) + "\n" + "Selected Plan of Member "
                + member.getMembershipPlan().getPlanName()
                + "\n"
                + "Total Cost of the Plan "
                + member.getMembershipPlan().getCost();
    }
}
