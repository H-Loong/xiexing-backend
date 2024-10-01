package com.hloong.xiexing.controller;


import com.hloong.xiexing.common.BaseResponse;
import com.hloong.xiexing.common.ErrorCode;
import com.hloong.xiexing.common.ResultUtil;
import com.hloong.xiexing.exception.BusinessException;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.vo.ChatMessagesVO;
import com.hloong.xiexing.service.ChatMessagesService;
import com.hloong.xiexing.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;

/**
 * 私聊消息控制器类
 *
 * @author H-Loong
 */
@RestController
@RequestMapping("/chat")
public class ChatMessageController {

    @Resource
    private ChatMessagesService chatMessagesService;

    @Resource
    private UserService userService;

    /**
     * 获取历史聊天记录
     *
     * @param senderId 发送者id
     * @param request
     * @return
     */
    @GetMapping("/history/{senderId}")
    public BaseResponse<List<ChatMessagesVO>> getHistoryMessages(@PathVariable("senderId") long senderId,
                                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long receiverId = loginUser.getId();
        List<ChatMessagesVO> historyMessages = chatMessagesService.getHistoryMessages(senderId, receiverId);
        return ResultUtil.success(historyMessages);
    }

}
