package com.hloong.xiexing.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hloong.xiexing.common.BaseResponse;
import com.hloong.xiexing.common.ErrorCode;
import com.hloong.xiexing.exception.BusinessException;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.request.UpdateTagsRequest;
import com.hloong.xiexing.model.domain.request.UserLoginRequest;
import com.hloong.xiexing.model.domain.request.UserRegisterRequest;
import com.hloong.xiexing.service.UserService;
import com.hloong.xiexing.common.ResultUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hloong.xiexing.constant.userConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 *
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,email)){
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, email);
        return ResultUtil.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        User user =  userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtil.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtil.success(safetyUser);
    }
    @GetMapping("/info")
    public BaseResponse<User> getUserInfo(@RequestParam(required = true) long userId){
        User user = userService.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtil.success(safetyUser);
    }
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, String userAccount, HttpServletRequest request){
        //鉴权 仅管理员可查询
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            userQueryWrapper.like("username",username);
        }
        if (StringUtils.isNotBlank(userAccount)){
            userQueryWrapper.like("userAccount",userAccount);
        }
        List<User> userList = userService.list(userQueryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(list);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            log.info("No logged-in user found, serving default recommendations.");
        }

        String redisKey = loginUser != null ?
                String.format("xiexing:user:recommend:%s", loginUser.getId()) :
                "xiexing:user:recommend:default";

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtil.success(userPage);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtil.success(userPage);
    }



    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtil.success(userList);
    }


    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtil.success(result);
    }
    @PostMapping("/updateTags")
    public BaseResponse<Boolean> updateTags(@RequestBody UpdateTagsRequest request){
        boolean result = userService.updateUserTags(request.getUserId(), request.getTags());
        return ResultUtil.success(result);
    }

    @PostMapping("upload")
    @ResponseBody
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file, Long id){
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String result = userService.uploadImage(file, id);
        return ResultUtil.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        //鉴权 仅管理员可删除
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtil.success(b);
    }

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtil.success(userService.matchUsers(num, user));
    }


}
