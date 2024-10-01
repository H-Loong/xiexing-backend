package com.hloong.xiexing.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户私聊消息表
 * @TableName chat_messages
 */
@TableName(value ="chat_messages")
@Data
public class ChatMessages implements Serializable {
    /**
     * 唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer chatId;

    /**
     * 发送者id
     */
    private Integer senderId;

    /**
     * 接收者id
     */
    private Integer receiverId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送时间
     */
    private Date timestamp;

    /**
     * 状态：0-未读，1-已读
     */
    private Integer readStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}