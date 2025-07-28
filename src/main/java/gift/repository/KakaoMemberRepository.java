package gift.repository;

import gift.entity.KakaoMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoMemberRepository extends JpaRepository<KakaoMember, Long> {
    Optional<KakaoMember> findByKakaoId(Long kakaoId);
}
