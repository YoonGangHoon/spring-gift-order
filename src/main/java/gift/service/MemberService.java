package gift.service;

import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.exception.MemberNotFoundException;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponseDto> findAll() {
        return memberRepository.findAll().stream()
                .map(m -> new MemberResponseDto(m.getId(), m.getKakaoId(), m.getNickname()))
                .toList();
    }

    public MemberResponseDto find(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException("id", memberId.toString()));

        return new MemberResponseDto(member.getId(), member.getKakaoId(), member.getNickname());
    }

    public void delete(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("id", memberId.toString()));
        memberRepository.delete(member);
    }

}
