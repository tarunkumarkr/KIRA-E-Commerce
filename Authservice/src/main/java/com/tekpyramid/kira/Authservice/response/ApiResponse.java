package com.tekpyramid.kira.Authservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {

    private String message;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private String accessToken;
    private LocalDateTime tokenExpiry;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;

    private T data;
}
