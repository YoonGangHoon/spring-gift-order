package gift.dto;

public record KakaoUserInfoResponseDto(
        Long id,
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            Profile profile
    ) {
        public record Profile(
                String nickname
        ) {}
    }
}
