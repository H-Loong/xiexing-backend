package com.hloong.xiexing.model.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTagsRequest {
    private Long userId;
    private List<String> tags;

    // Getters and Setters
}
