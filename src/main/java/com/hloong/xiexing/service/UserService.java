package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
*
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-04-17 16:11:22
*/
    public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount       用户账号
     * @param userPassword      用户密码
     * @param checkPassword     校验密码
     * @return                  新用户id
     */

    long userRegister(String userAccount, String userPassword, String checkPassword, String email);

    /**
     * 用户登录
     *
     * @param userAccount       用户账号
     * @param userPassword      用户密码
     * @return                  脱敏后的用户信息
     */
        User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    List<User> searchUsersByTags(List<String> tagNameList);

    User getLoginUser(HttpServletRequest request);

    int updateUser(User user, User loginUser);

    String uploadImage(MultipartFile file, long id);

    boolean updateUserTags(Long userId, List<String> tags);

//    User getUserInfo(long id);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    List<User> matchUsers(long num, User loginUser);
}
