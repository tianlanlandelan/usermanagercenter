package com.originaldreams.usermanager.auth;

import com.originaldreams.usermanager.service.UserService;
import com.originaldreams.usermanager.util.TokenDetail;
import com.originaldreams.usermanager.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {
    private String tokenHeader = "token";

    private TokenDetail tokenDetail;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) res;

        String authToken = httpRequest.getHeader(this.tokenHeader);

        String username = this.tokenUtil.getSubjectFromToken(authToken);

        // 如果上面解析 token 成功并且拿到了 username 并且本次会话的权限还未被写入
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 用 UserDetailsService 从数据库中拿到用户的 UserDetails 类
            // UserDetails 类是 Spring Security 用于保存用户权限的实体类
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            // 检查用户带来的 token 是否有效
            // 包括 token 和 userDetails 中用户名是否一样， token 是否过期， token 生成时间是否在最后一次密码修改时间之前
            // 若是检查通过
            if (this.tokenUtil.validateToken(authToken, userDetails)) {
                // 生成通过认证
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                // 将权限写入本次会话
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            if (!userDetails.isEnabled()){
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().print("{\"code\":\"452\",\"data\":\"\",\"message\":\"账号处于黑名单\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }

}
