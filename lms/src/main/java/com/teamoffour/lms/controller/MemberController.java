package com.teamoffour.lms.controller;


import com.teamoffour.lms.service.MemberInterface;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;

@RestController
public class MemberController {
    private final MemberInterface memberInterface;

    @Autowired
    public MemberController(MemberInterface memberInterface) {
        this.memberInterface = memberInterface;
    }


    @PostMapping(value = "/lms/registerMember")
    public String registerMember(@RequestBody RegisterMemberRequest registerMemberRequest) throws ServerException {
        return memberInterface.registerMember(registerMemberRequest);
    }

}
