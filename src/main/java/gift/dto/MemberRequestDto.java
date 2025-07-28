package gift.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberRequestDto(
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname
) {
}
