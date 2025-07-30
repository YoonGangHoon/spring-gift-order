package gift.exception.kakao;

public class KakaoOAuthException extends RuntimeException {

    public KakaoOAuthException(String message) {
        super(message);
    }

    public KakaoOAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
