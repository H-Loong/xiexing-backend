package com.hloong.xiexing.controller;


import com.hloong.xiexing.common.BaseResponse;
import com.hloong.xiexing.common.ErrorCode;
import com.hloong.xiexing.common.ResultUtil;
import com.hloong.xiexing.exception.BusinessException;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.vo.FriendVO;
import com.hloong.xiexing.service.FollowRelationshipService;
import com.hloong.xiexing.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;

/**
 * 好友列表控制器类
 */
@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Resource
    private FollowRelationshipService followRelationshipService;

    @Resource
    private UserService userService;

    /**
     * 获取所有好友（关注/粉丝）列表
     * @param request
     * @return
     */
    @GetMapping("/all")
    public BaseResponse<List<FriendVO>> getAllFriends(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<FriendVO> friends = followRelationshipService.getAllFriends(loginUser);
        return ResultUtil.success(friends);
    }

    /**
     * 获取当前用户粉丝
     * @param request
     * @return 粉丝集合
     */
    @GetMapping("/fans")
    public BaseResponse<List<User>> getFans(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<User> fans = followRelationshipService.getFans(loginUser);
        return ResultUtil.success(fans);
    }

    /**
     * 获取当前用户关注用户
     * @param request
     * @return 粉丝集合
     */
    @GetMapping("/followers")
    public BaseResponse<List<User>> getFollowers(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<User> followers = followRelationshipService.getFollowers(loginUser);
        return ResultUtil.success(followers);
    }

}
