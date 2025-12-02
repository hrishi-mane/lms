package com.teamoffour.lms.service;

import com.teamoffour.lms.service.dto.RegisterMemberRequest;

import java.rmi.ServerException;

public interface MemberInterface {
    String registerMember(RegisterMemberRequest registerMemberRequest) throws ServerException;
}
