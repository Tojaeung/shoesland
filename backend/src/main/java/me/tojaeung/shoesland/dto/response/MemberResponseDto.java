package me.tojaeung.shoesland.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.tojaeung.shoesland.entity.Member;

public class MemberResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Login {
        private String accessToken;
        private Member member;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Refresh {
        private String accessToken;
        private Member member;
    }
}
