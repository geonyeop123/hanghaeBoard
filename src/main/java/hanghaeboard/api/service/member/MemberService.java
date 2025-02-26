package hanghaeboard.api.service.member;

import hanghaeboard.api.service.member.response.FindMember;
import hanghaeboard.domain.member.Member;
import hanghaeboard.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public FindMember findMemberById(Long id){
        Member findMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회된 회원이 없습니다."));

        return FindMember.of(findMember);
    }

}
