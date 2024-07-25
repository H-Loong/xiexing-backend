package com.hloong.xiexing.model.domain.request;


import lombok.Data;

/**
 * 用户提交请求体
 */

@Data
public class UserSubmitRequest {

    private String username;
    private String avatarUrl;
    private String phone;
    private String email;
    private Integer gender;

}
