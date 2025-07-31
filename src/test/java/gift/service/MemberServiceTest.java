package gift.service;

import gift.dto.member.MemberResponseDto;
import gift.entity.Member;
import gift.exception.MemberNotFoundException;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 전체_회원_조회_성공() {
        Member member1 = new Member(123L, "닉네임1", "access-token", "refresh-token", 0);
        Member member2 = new Member(456L, "닉네임2", "access-token", "refresh-token", 0);
        when(memberRepository.findAll()).thenReturn(List.of(member1, member2));

        List<MemberResponseDto> results = memberService.findAll();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).nickname()).isEqualTo("닉네임1");
        assertThat(results.get(1).kakaoId()).isEqualTo(456);
        verify(memberRepository).findAll();
    }

    @Test
    void 회원_단건_조회_성공() {
        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);
        when(memberRepository.findById(123L)).thenReturn(Optional.of(member));

        MemberResponseDto dto = memberService.find(123L);

        assertThat(dto.nickname()).isEqualTo("닉네임");
        verify(memberRepository).findById(123L);
    }

    @Test
    void 회원_단건_조회_실패_예외() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.find(1L));
    }

    @Test
    void 회원_삭제_성공() {
        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);
        when(memberRepository.findById(123L)).thenReturn(Optional.of(member));

        memberService.delete(123L);

        verify(memberRepository).delete(member);
    }

    @Test
    void 회원_삭제_실패_예외() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.delete(1L));
    }
}