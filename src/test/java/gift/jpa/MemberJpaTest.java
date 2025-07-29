package gift.jpa;

import gift.entity.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class MemberJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_저장_및_조회_성공() {
        Member newMember = new Member(1L, "test_nickname", "access_token", "refresh_token", 1);
        Member saved = memberRepository.save(newMember);

        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getNickname()).isEqualTo("test_nickname");
    }

    @Test
    void 조회_실패() {
        Optional<Member> result = memberRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void 회원_삭제() {
        Member member = new Member(1L, "test_nickname", "access_token", "refresh_token", 1);
        entityManager.persist(member);
        entityManager.flush();
        entityManager.clear();

        memberRepository.delete(member);

        Optional<Member> result = memberRepository.findById(1L);
        assertThat(result).isEmpty();
    }
}
