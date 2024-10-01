package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.FollowRelationship;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.vo.FriendVO;

import java.util.List;

/**
* @author lenovo
* @description 针对表【follow_relationship(粉丝关注关系)】的数据库操作Service
* @createDate 2024-09-22 21:33:39
*/
public interface FollowRelationshipService extends IService<FollowRelationship> {

    String followUser(Long id, User loginUser);

    String unFollowUser(Long id, User loginUser);

    long getFansNum(Long id);

    long getFollowNum(Long id);

    boolean isFans(long id, User loginUser);

    List<FriendVO> getAllFriends(User loginUser);

    List<User> getFans(User loginUser);

    List<User> getFollowers(User loginUser);

    void setDefaultFans(long userId);
}
