package com.nguyenthanhbang.Social_media.dto.request;

import lombok.Getter;

@Getter
public class BlockRequest {
    private String reason;

    public String getReason() {
        return reason;
    }
}

