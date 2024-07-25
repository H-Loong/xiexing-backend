package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.model.domain.dto.TeamQuery;
import com.hloong.xiexing.model.domain.request.TeamJoinRequest;
import com.hloong.xiexing.model.domain.request.TeamQuitRequest;
import com.hloong.xiexing.model.domain.request.TeamUpdateRequest;
import com.hloong.xiexing.model.domain.vo.TeamUserVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author H-Loong
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-05-17 22:55:48
*/
public interface TeamService extends IService<Team> {

    /**
     * @author H-Loong
     * @param team  队伍
     * @param loginUser  创建用户
     * @return
     */
    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean deleteTeam(long id, User loginUser);
}
