package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工登录
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1. 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. 根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3. 无查询结果返回登陆失败
        if (emp == null) {
            return R.error("登陆失败");
        }
        //4. 密码查询对比，不一致返回登陆身边
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        //5. 查看员工状态，如果为禁用状态，则返回已禁用的结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //6. 登陆成功，员工id存入Session并进行登录成功结果
        request.getSession().setAttribute("employee", employee.getId());
        return R.success(emp);
        /**
         * 1. 将页面提交的密码进行md5加密处理
         * 2. 根据页面提交的用户名查询数据库
         * 3. 无查询结果返回登陆失败
         * 4. 密码查询对比，不一致返回登陆身边
         * 5. 查看员工状态，如果为禁用状态，则返回已禁用的结果
         * 6. 登陆成功，员工id存入Session并进行登录成功结果
         */
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }
}
