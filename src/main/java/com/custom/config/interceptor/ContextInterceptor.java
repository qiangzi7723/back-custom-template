package com.custom.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.entity.common.HeaderEntity;
import com.custom.request.Context;
import com.custom.util.IPUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sun.jvm.hotspot.memory.HeapBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContextInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Map map = new HashMap();
        //得到枚举类型的参数名称，参数名称若有重复的只能得到第一个
        if(request.getMethod().equals("GET")){
            Enumeration enums = request.getParameterNames();
            while (enums.hasMoreElements())
            {
                String paramName = (String) enums.nextElement();
                String paramValue = request.getParameter(paramName);

                //形成键值对应的map
                map.put(paramName, paramValue);
            }
        }else{
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if(!StrUtil.isBlank(body)){
                map = new ObjectMapper().readValue(body, HashMap.class);
            }
        }

        // 将头部信息存储进上下文
        HeaderEntity headerEntity = new HeaderEntity();
        headerEntity.setApi(request.getRequestURL().toString());
        headerEntity.setIp(IPUtil.getIpAddr(request));
        headerEntity.setMethod(request.getMethod());
        headerEntity.setReferer(request.getHeader("REFERER"));
        headerEntity.setAuthorization(request.getHeader("Authorization"));
        Context.setHeader(headerEntity);

        Context.set("requestParams", JSONObject.parseObject(JSONObject.toJSONString(map)));


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Context.remove();
    }
}
