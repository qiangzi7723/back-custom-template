package com.custom.config.auth;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.entity.common.JwtUserEntity;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.util.JSONUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private boolean noNeedAuth(HttpServletRequest request){
        String url = request.getRequestURL().toString();
        System.out.println("请求地址："+url);
        List<String> list = new ArrayList<String>();
        list.add("swagger");

        for(String attribute : list) {
            if(url.indexOf(attribute)>-1){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(noNeedAuth(request)) return true;

        String authorization = request.getHeader("Authorization");

        // 为了让swagger也能正常请求
        String refer = request.getHeader("REFERER");
        if(refer!=null && refer.contains("swagger")){
            // 说明是从swagger请求的接口，给一个默认的JWT
            authorization = "Bear eyJhbGciOiJIUzUxMiJ9.eyJkYXRhIjp7ImlkIjoyMCwidWlkIjoxMDAxLCJuaWNrTmFtZSI6IlN3YWdnZXLpu5jorqTnlKjmiLciLCJhdmF0YXIiOiJ6enEucG5nIiwibW9iaWxlIjoiMTg4MjYxMzk2NjAifSwiZXhwIjoxNTkzMTQ2MDIzMCwic3ViIjoiIn0.601DKorr-N_KTCPM-jSfn6QF9zkWuB6XXGFcWdBfiM1UYI2nOsu_-eQyG-BAmuIFR7mPbazPgGdDbtHC1lESgw";
        }

        try{
            String splitToken = authorization.substring(5);
            Claims claims = JwtToken.getUserInfo(splitToken);
            Object obj = claims.get("data");
            JSONObject data = JSONUtil.iterateJSON(JSONUtil.toJSON(obj));
            JwtUserEntity user = new JwtUserEntity();
            BeanUtil.fillBeanWithMapIgnoreCase(data, user, false);
            Context.setUser(user);
        }catch(Exception e){
            noAuth(request,response);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", ErrorEnum.E_503.getErrorCode());
            jsonObject.put("msg", ErrorEnum.E_503.getErrorMsg());
            out.println(jsonObject);
        } catch (Exception e) {
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
