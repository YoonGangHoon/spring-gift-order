package gift.entity;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private String nickname;

    private String accessToken;

    private String refreshToken;

    private int tokenExpiresAt;

    protected Member() {
    }

    public Member(Long id){
        this.id = id;
    }

    public Member(Long kakaoId, String nickname, String accessToken, String refreshToken, int tokenExpiresAt) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public Long getKakaoId(){
        return kakaoId;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
