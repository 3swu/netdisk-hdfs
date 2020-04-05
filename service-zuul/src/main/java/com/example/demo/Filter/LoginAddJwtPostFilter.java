package com.example.demo.Filter;

import com.example.demo.Entity.User;
import com.example.demo.Util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Component
public class LoginAddJwtPostFilter extends ZuulFilter {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    private String loginURL;

    private Integer expDays;

    public LoginAddJwtPostFilter(@Value("${data-filter.user-login-path}") String path,
                                 @Value("${jwt.exp-days}") Integer expDays) {
        this.loginURL = path;
        this.expDays = expDays;
    }


    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return this.loginURL.equals(request.getRequestURI());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            InputStream stream = ctx.getResponseDataStream();
            String body = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            if (body.length() == 0) return null; //登录失败 返回空字符串
            User user = objectMapper.readValue(body, User.class);
            if (user != null) {
                HashMap<String, Object> jwtClaims = new HashMap<>();
                jwtClaims.put("userid", user.getId());
                jwtClaims.put("username", user.getUsername());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, this.expDays);
                Date expDate = calendar.getTime();
                String token = jwtUtil.createJWT(expDate, jwtClaims);
                ctx.setResponseBody(body);
                ctx.addZuulResponseHeader("token", token);
            }


        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
