package com.hloong.xiexing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hloong.xiexing.model.domain.UserTeam;
import com.hloong.xiexing.service.UserTeamService;
import com.hloong.xiexing.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author H-Loong
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-05-17 22:55:48
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




