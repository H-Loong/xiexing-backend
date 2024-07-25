package com.hloong.xiexing.service;

import com.hloong.xiexing.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void TestAddUser(){
        User user = new User();
        user.setUsername("mhl");
        user.setUserAccount("123");
        user.setAvatarUrl("https://s2.loli.net/2023/04/14/OREnG52wk1Z3IXf.jpg");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("13213");
        userService.save(user);

    }

//    @Test
//    void userRegister() {
//        String userAccount = "Helen";
//        String userPassword = "";
//        String checkPassword = "12345678";
//        long result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//        userAccount = "mhl";
//        userPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//        userAccount = "ss  * asd ";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//        userAccount = "aaaaaaaa";
//        checkPassword = "321654654";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//        userAccount = "mhl123";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//        userAccount = "Helen";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertTrue(result > 0);
//
//    }

//    @Test
//    void testSearchUserByTags(){
//        List<String> tagNameList = Arrays.asList("java", "python");
//        List<User> userList = userService.searchUsersByTags(tagNameList);
//        Assertions.assertNotNull(userList);
//    }
}