package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.ChatMessages;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hloong.xiexing.model.domain.ResultMessage;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.vo.ChatMessagesVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @author lenovo
* @description 针对表【chat_messages(用户私聊消息表)】的数据库操作Service
* @createDate 2024-09-22 21:33:35
*/
public interface ChatMessagesService extends IService<ChatMessages> {

    boolean getReadStatus(User sendUser, User receiveUser);

    @Transactional
    List<ChatMessagesVO> getHistoryMessages(long senderId, long receiverId);

    boolean updateUnReadMsg(List<Map<Long, Long>> chatIds);

    @Transactional
    void saveMessages(List<ResultMessage> messageObjs);
}
