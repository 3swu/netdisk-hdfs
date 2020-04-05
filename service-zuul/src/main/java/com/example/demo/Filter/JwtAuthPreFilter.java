package com.example.demo.Filter;

import com.example.demo.Util.JwtUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "data-filter")
public class JwtAuthPreFilter extends ZuulFilter {


    @Autowired
    JwtUtil jwtUtil;

    private List<String> whitelist = new ArrayList<>();

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRequestURI();
        for (var pattern : whitelist){
            if (url.equals(pattern))
                return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("token");
        Claims claims;
        try {
            claims = jwtUtil.parseJWT(token);
            ctx.setSendZuulResponse(true);
            ctx.addZuulRequestHeader("id", claims.get("userid").toString());
        }
        catch (ExpiredJwtException e) {
            ctx.setSendZuulResponse(false);
            responseError(ctx, "token expired");
        }
        catch (Exception e) {
            ctx.setSendZuulResponse(false);
            responseError(ctx, "valid failed");
        }
        return null;
    }

    private void responseError(RequestContext ctx, String message) {
        HttpServletResponse response = ctx.getResponse();
        ctx.setResponseBody(message);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=utf-8");
    }
}
