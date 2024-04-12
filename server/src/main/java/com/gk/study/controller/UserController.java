package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import com.gk.study.utils.RandomUtils;
import com.gk.study.utils.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
/**
 * @author lengqin1024(微信)
 * @email net936@163.com
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        List<User> list =  userService.getUserList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId){
        User user =  userService.getUserDetail(Long.valueOf(userId));
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    // 后台用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user) throws NoSuchAlgorithmException {
        User selectUser = userService.getUserByUserName(user.getUsername());
        if(selectUser == null){
            return new APIResponse(ResponeCode.FAIL, "用户不存在");
        }
        String encPwd = SecureUtil.md5(user.getPassword(), selectUser.getSalt());
        if(selectUser.getPassword().equalsIgnoreCase(encPwd)) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", selectUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user) throws NoSuchAlgorithmException {
        User selectUser = userService.getUserByUserName(user.getUsername());
        if(selectUser == null){
            return new APIResponse(ResponeCode.FAIL, "用户不存在");
        }
        String encPwd = SecureUtil.md5(user.getPassword(), selectUser.getSalt());
        if(selectUser.getPassword().equalsIgnoreCase(encPwd)) {
            // 加积分
            selectUser.setScore(selectUser.getScore() + 10);
            userService.updateUser(selectUser);
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", selectUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户注册
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException, NoSuchAlgorithmException {

        if (!StringUtils.isEmpty(user.getUsername())
                && !StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getRePassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            String salt = RandomUtils.randomStr(8);
            String encPwd = SecureUtil.md5(user.getPassword(), salt);
            // 设置盐
            user.setSalt(salt);
            // 保存加密后的密码
            user.setPassword(encPwd);
            String token = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(token);

            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            // 设置角色
            user.setRole(String.valueOf(User.NormalUser));
            // 设置状态
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException, NoSuchAlgorithmException {

        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            String salt = RandomUtils.randomStr(8);
            String encPwd = SecureUtil.md5(user.getPassword(), salt);
            // 设置密码
            user.setPassword(encPwd);
            String token = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(token);

            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            userService.deleteUser(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // update不能修改密码，故置空
        user.setPassword(null);
        String avatar = saveAvatar(user);
        if(!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        System.out.println(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }


    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser =  userService.getUserDetail(user.getId());
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException, NoSuchAlgorithmException {
        User user =  userService.getUserDetail(Long.valueOf(userId));
        if(user.getRole().equals(String.valueOf(User.NormalUser))) {
            String salt = RandomUtils.randomStr(8);
            String md5Pwd = SecureUtil.md5(password, salt);
            if(user.getPassword().equals(md5Pwd)){
                user.setPassword(DigestUtils.md5DigestAsHex((newPassword + salt).getBytes()));
                userService.updateUser(user);
            }else {
                return new APIResponse(ResponeCode.FAIL, "原密码错误");
            }
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }

    }

    public String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {

            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            user.avatar = newFileName;
        }
        return newFileName;
    }
}
