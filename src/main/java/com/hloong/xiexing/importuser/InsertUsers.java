package com.hloong.xiexing.importuser;

import com.hloong.xiexing.mapper.UserMapper;
import com.hloong.xiexing.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.util.StopWatch;
import org.springframework.stereotype.Component;

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("test");
            user.setUserAccount("H-Loong");
            user.setAvatarUrl("https://xiexing-1325079952.cos.ap-beijing.myqcloud.com/avatar/38211719131350917.png");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
