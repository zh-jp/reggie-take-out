package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        //定义不需要的处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/sendMail",   // 邮箱验证码
                "/user/login",       //移动端登录
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        //2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3. 若不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        //4.1 判断员工登录状态，若已经登录则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }
        //4.2 判断用户登录状态，若已经登录则直接放行
        if (request.getSession().getAttribute("user") != null) {
            Long UsrId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(UsrId);

            filterChain.doFilter(request, response);
            return;
        }
        //5. 如果未登录则返回登录界面，通过输出流方式向客户页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
        /**
         *     1. 获取本次请求的URI
         *     2. 判断本次请求是否需要处理
         *     3. 若不需要处理，则直接放行
         *     4. 判断登录状态，若已经登录则直接放行
         *     5. 如果未登录则返回登录界面
         */
    }

    /**
     * 路径匹配，本次请求是否需要放行
     *
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
