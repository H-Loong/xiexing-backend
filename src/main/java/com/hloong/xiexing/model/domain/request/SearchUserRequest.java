package com.hloong.xiexing.model.domain.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 用户提交请求体
 */

@Data
public class SearchUserRequest {

    private String username;
    private String avatarUrl;
    private String phone;
    private String email;
    private Integer gender;
    private Integer id;
    private String userAccount;
    private Integer userStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    private Integer userRole;

}
