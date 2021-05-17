package com.custom.config.interceptor;

import com.custom.config.exception.CommonJsonException;
import com.custom.db.redis.key.Key;
import com.custom.db.redis.service.LuaRedisService;
import com.custom.db.redis.service.StringRedisService;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Component
public class UserLockInterceptor implements HandlerInterceptor {
    @Autowired
    LuaRedisService luaRedisService;

    @Autowired
    StringRedisService stringRedisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!needLock(request)) return true;

        int uid = Context.getUser().getUid();
        String key = Key.userLock(String.valueOf(uid));
        // 加锁
        boolean lock = luaRedisService.tryLock(key,"1",60);
        if(!lock){
            // 触发了锁的限制
            throw new CommonJsonException(ErrorEnum.E_REQUEST_LIMIT);
        }else{
            Context.setKey("userLockInterceptor", true);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(needLock(request)){
            boolean status = Context.getKey("userLockInterceptor");
            if(status){
                int uid = Context.getUser().getUid();
                String key = Key.userLock(String.valueOf(uid));
                stringRedisService.remove(key);
            }
        }
    }

    private boolean needLock(HttpServletRequest request){
        String currentApi = request.getRequestURI();
        String currentMethod = request.getMethod();

        // 满足这两个条件的接口，都会被自动加锁
        List<String> needLockList = Arrays.asList("/box/", "/prop/", "/boot/register", "/building/", "/task/", "/fullLevel/");
        String method = "POST";

        boolean needLock = false;
        for(int i=0;i<needLockList.size();i++){
            String keyWords = needLockList.get(i);
            if(currentApi.contains(keyWords) && currentMethod.equals(method)){
                // 说明是关键接口 且方法一致
                needLock = true;
                break;
            }
        }

        return needLock;
    }
}
