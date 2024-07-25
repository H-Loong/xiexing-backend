package com.hloong.xiexing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hloong.xiexing.common.ErrorCode;
import com.hloong.xiexing.exception.BusinessException;
import com.hloong.xiexing.model.domain.User;
import com.hloong.xiexing.service.UserService;
import com.hloong.xiexing.mapper.UserMapper;
import com.hloong.xiexing.utils.AlgorithmUtils;
import com.hloong.xiexing.utils.TencentCOSUploadFileUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hloong.xiexing.constant.userConstant.ADMIN_ROLE;
import static com.hloong.xiexing.constant.userConstant.USER_LOGIN_STATE;

/**
 *
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-04-17 16:11:22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TencentCOSUploadFileUtil tencentCOSUploadFileUtil;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "mhl";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String email) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,email)) {
            //todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        String patternEmail = "^[1-9][0-9]{4,10}@qq\\.com$";
        Matcher matcherEmail = Pattern.compile(patternEmail).matcher(email);
        if (!matcherEmail.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "QQ邮箱格式不正确");
        }
        //账户不能包含特殊字符
        // 定义正则表达式模式，只允许字母、数字和下划线
        String patternAccount = "^[a-zA-Z0-9_]+$";
        Matcher matcherAccount = Pattern.compile(patternAccount).matcher(userAccount);
        if (!matcherAccount.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号只允许字母、数字和下划线");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        //账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setEmail(email);
        user.setTags("[\"暂无\"]");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册异常");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 6) {
            return null;
        }
        //账户不能包含特殊字符
        String pattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (!matcher.find()) {
            return null;
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //检查账号是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        //3.用户脱敏
        User safeUser = getSafetyUser(user);
        //4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        return safeUser;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setTags(originUser.getTags());
        safeUser.setProfile(originUser.getProfile());
        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        //移除用户登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     *   根据标签搜索用户。
     * @param tagNameList  用户要搜索的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * sql方法实现搜索用户
     */
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        //拼接tag
//        // like '%Java%' and like '%Python%'
//        for (String tagList : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagList);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return  userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public int updateUser(User user, User loginUser){
        long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员，可以修改任意用户
        //如果不是管理员，只能修改自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);

    }
    /**
     * @methodName uploadPictures
     * @effect: 图片上传
     */
    @Override
    public String uploadImage(MultipartFile file, long id) {
        //调用uploadFile方法，将文件上传至腾讯云存储桶
        String avatarUrl = tencentCOSUploadFileUtil.uploadFile(file);
        User user = userMapper.selectById(id);
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
        return avatarUrl;
    }

    @Override
    public boolean updateUserTags(Long userId, List<String> tags) {
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (tags == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 将标签列表转换为 JSON 字符串
        Gson gson = new Gson();
        String tagsJson = gson.toJson(tags);
        // 创建一个 User 对象并设置新的标签
        User user = new User();
        user.setTags(tagsJson);

        // 使用 UpdateWrapper 来指定更新条件
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);

        // 执行更新操作
        return userMapper.update(user, updateWrapper) > 0;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null || user.getUserRole()  != ADMIN_ROLE){
            return false;
        }
        return true;
    }

    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(Pair.of(user, distance));
        }

        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .toList();
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }



}





