package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.UserOnlineStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
* @author lenovo
* @description 针对表【user_online_status(用户在线状态表)】的数据库操作Service
* @createDate 2024-09-22 21:33:42
*/
public interface UserOnlineStatusService extends IService<UserOnlineStatus> {

    Integer getUserStatus(Long id);

    Integer setUserStatus(Long id, Integer status);

    @Transactional
    void updateUserOnlineStatus(Set<Long> onlineUserIds);
}
