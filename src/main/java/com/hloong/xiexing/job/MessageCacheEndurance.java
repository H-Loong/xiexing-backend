package com.hloong.xiexing.job;


import com.alibaba.fastjson.JSON;
import com.hloong.xiexing.common.ChatEndpoint;
import com.hloong.xiexing.model.domain.ChatMessages;
import com.hloong.xiexing.model.domain.ResultMessage;
import com.hloong.xiexing.service.ChatMessagesService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定时任务持久化缓存中的消息
 *
 * @author H-Loong
 */
@Component
@Slf4j
public class MessageCacheEndurance {

    @Resource
    private ChatMessagesService chatMessagesService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 4 * * ? ")
    @Transactional
    public void doMessageCacheEndurance() {
        log.info("开始持久化用户消息");
        HashSet<String> chatUserKeys = ChatEndpoint.chatUserKeys;
        for (String chatUserKey : chatUserKeys) {
            List<ChatMessages> chatMessagesList = new ArrayList<>();
            Set<String> messageKeys = redisTemplate.opsForList().getOperations().keys(chatUserKey);
            if (messageKeys == null || messageKeys.isEmpty()) {
                return;
            }

            String messageKey = null;
            for (String key : messageKeys) {
                messageKey = key;
            }

            Long size = redisTemplate.opsForList().size(messageKey);
            if (size == null || size <= 0) {
                continue;
            }
            List<Object> resultMessages = redisTemplate.opsForList().range(messageKey, 0, size);
            if (resultMessages == null || resultMessages.isEmpty()) {
                continue;
            }

            for (Object resultMessageObj : resultMessages) {
                String jsonString = JSON.toJSONString(resultMessageObj);
                ChatMessages resultMessage = JSON.parseObject(jsonString, ChatMessages.class);
                ChatMessages chatMessages = new ChatMessages();
                BeanUtils.copyProperties(resultMessage, chatMessages);
                chatMessagesList.add(chatMessages);
            }

            boolean saveResult = chatMessagesService.saveBatch(chatMessagesList);
            if (saveResult) {
                redisTemplate.delete(messageKey);
            }
        }
        ChatEndpoint.chatUserKeys.clear();
        log.info("完成持久化用户消息");
    }

}
