package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;

import java.rmi.ServerException;
import java.util.List;

public interface MemberInterface {
    String registerMember(RegisterMemberRequest registerMemberRequest) throws ServerException;


    List<Member> getAllMembers();
}
