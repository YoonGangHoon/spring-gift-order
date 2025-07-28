package gift.controller;

import gift.config.LoginMember;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/myInfo")
    public ResponseEntity<MemberResponseDto> getMyInfo(
            @LoginMember Member member
    ){
        MemberResponseDto responseDto = memberService.find(member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<MemberResponseDto> deleteMyInfo(
            @LoginMember Member member
    ){
        memberService.delete(member.getId());
        return ResponseEntity.noContent().build();
    }

}
