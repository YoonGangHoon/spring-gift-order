package gift.utils;

import gift.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class E2ETestUtils {

    public String 테스트용_고정_토큰() {
        // JWT 구조는 가짜여도 되고, 실제로 파싱만 통과하면 됩니다.
        // 서버에서 검증을 우회하거나 stub 처리해야 합니다.
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.cDF0ToCw0beej_PcZZQAhLPSXPZp77-iY8CHOJ9kGLk";
    }

    public Member 테스트용_회원(){
        return new Member(1L, "테스트", "access-token", "refresh-token", 0);
    }
}