package com.reggie.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.MailService;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    // Redis服务类
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${reggie.sms.code_length}")
    private Integer code_length;

    @Value("${reggie.sms.signName}")
    private String signName;

    @Value("${reggie.sms.templateCode}")
    private String templateCode;
    @Value("${reggie.aliUser.accessKeyId}")
    private String accessKeyId;

    @Value(value = "${reggie.aliUser.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 发送短信验证码
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)) {
            Integer code = ValidateCodeUtils.generateValidateCode(code_length);
            SMSUtils.sendMessage(accessKeyId, accessKeySecret, signName, templateCode, phone, code.toString());
            request.getSession().setAttribute(phone, code);
            log.info("code:{}", code);
            log.info("phone:{}", phone);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 发送邮箱验证码
     *
     * @param user
     * @param
     * @return
     */
    @PostMapping("/sendMail")
    public R<String> sendMail(@RequestBody User user) {
        String mail = user.getMail();
        if (!StringUtils.isEmpty(mail)) {
            Integer code = ValidateCodeUtils.generateValidateCode(code_length);
            //mailService.sendSimpleMail(mail, "用户登录验证码", code.toString());

            // 在Session中设置验证码
            // request.getSession().setAttribute(mail, code);

            // 在Redis中设置验证码
            redisTemplate.opsForValue().set(mail, code, 5L, TimeUnit.MINUTES);
            log.info("code:{}, mail:{}", code, mail);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 获取手机号
     * 获取验证码
     * 从Session中获取保存的验证码
     * 进行验证码的比对
     * 比对成功，说明登陆成功
     * 判断当前用户是否为新用户，若是则完成注册
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpServletRequest request) {
        String mail = map.get("mail");
        String code = map.get("code");
        // Object object = request.getSession().getAttribute(mail);
        Object object = redisTemplate.opsForValue().get(mail);
        if (object != null && object.toString().equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail, mail);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setMail(mail);
                userService.save(user);
                user = userService.getOne(queryWrapper);

            }
            request.getSession().setAttribute("user", user.getId());
            redisTemplate.delete(mail);
            return R.success(user);
        }
        log.info(map.toString());
        return R.error("验证码有误，请重试！");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }
}
