package com.teamoffour.lms.controller;


import com.teamoffour.lms.service.MemberInterface;
import com.teamoffour.lms.service.dto.MemberDTO;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;
import java.util.List;

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


    @GetMapping(value = "/lms/getAllMembers")
    public List<MemberDTO> getAllMembers() {
        return memberInterface.getAllMembers();
    }

}
