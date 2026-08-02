package com.nguyenthanhbang.Social_media.common.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails {
    private int status;
    private String message;
    private String error;
    private String path;
    private Instant timestamp;
}