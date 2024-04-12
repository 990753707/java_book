package com.gk.study.service.impl;


import com.gk.study.entity.User;
import com.gk.study.service.UserService;
import com.gk.study.utils.RandomUtils;
import com.gk.study.utils.SecureUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {


    @Autowired
    private UserService userService;


    @Test
    public void createUser() throws NoSuchAlgorithmException {
        User user = new User();
        user.setSalt(RandomUtils.randomStr(8));
        user.setUsername("admin");
        user.setEmail("1@admin.com");
        user.setMobile("10086");
        user.setRole("3");
        user.setStatus("0");
        user.setScore(0);
        user.setPassword(SecureUtil.md5("123456",user.getSalt()));
        userService.createUser(user);
    }

    public static void main(String[] args) {
        System.out.println(RandomUtils.uuid());
    }
}