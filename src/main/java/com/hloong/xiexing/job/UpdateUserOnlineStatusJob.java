package com.hloong.xiexing.job;

import com.hloong.xiexing.model.domain.enums.UserOnlineStatusEnum;
import com.hloong.xiexing.service.UserOnlineStatusService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Set;

/**
 * 更新用户在线状态定时任务
 *
 * @author H-Loong
 */
@Component
@Slf4j
public class UpdateUserOnlineStatusJob {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserOnlineStatusService userOnlineStatusService;

    // 每三天在凌晨五点更新用户在线状态
    @Scheduled(cron = "0 * * * * ? ")
    public void doUpdateStatus() {
        Set<String> userOnlineStatusKeys = stringRedisTemplate.keys("user:onlineStatus*");
        if (userOnlineStatusKeys == null || userOnlineStatusKeys.isEmpty()) {
            return;
        }

        // 遍历获取在线用户id
        Set<Long> onlineUserIds = new HashSet<>();
        for (String onlineStatusKey : userOnlineStatusKeys) {
            String status = stringRedisTemplate.opsForValue().get(onlineStatusKey);
            Long userId = Long.valueOf(onlineStatusKey.substring(18));
            if (UserOnlineStatusEnum.ONLINE.getValue().equals(status)) {
                onlineUserIds.add(userId);
            }
        }

        // 持久化用户在线状态
        userOnlineStatusService.updateUserOnlineStatus(onlineUserIds);
        log.info("==========执行更新用户在线状态==========");
    }

}
