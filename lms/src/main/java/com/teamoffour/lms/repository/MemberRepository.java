package com.teamoffour.lms.repository;


import com.teamoffour.lms.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Long save(Member member);
    Optional<Member> findMemberById(Long memberId);
}
