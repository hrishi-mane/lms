package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Member;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class MemberRepositoryImpl implements MemberRepository {
    private final Map<Long, Member> members = new HashMap<>();

    @Override
    public Long save(Member member) {
        members.put(member.getId(), member);
        return member.getId();
    }

    @Override
    public Optional<Member> findMemberById(Long id) {
        return Optional.ofNullable(members.get(id));
    }
}
