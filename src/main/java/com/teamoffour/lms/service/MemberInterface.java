package com.teamoffour.lms.service;

import com.teamoffour.lms.service.dto.MemberDTO;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;

import java.rmi.ServerException;
import java.util.List;

public interface MemberInterface {
    String registerMember(RegisterMemberRequest registerMemberRequest) throws ServerException;

    List<MemberDTO> getAllMembers();
}
