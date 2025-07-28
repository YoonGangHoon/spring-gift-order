package gift.entity;

import jakarta.persistence.*;

@Entity
public class KakaoMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    private String accessToken;

    private String refreshToken;

    private int tokenExpiresAt;

    protected KakaoMember() {
    }

    public KakaoMember(Long kakaoId, String accessToken, String refreshToken, int tokenExpiresAt) {
        this.kakaoId = kakaoId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public Long getId() {
        return id;
    }
}
