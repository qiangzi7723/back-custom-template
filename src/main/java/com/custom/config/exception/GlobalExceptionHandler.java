package com.custom.config.exception;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.dao.common.IllegalRequestDao;
import com.custom.db.redis.key.IllegalRequest;
import com.custom.db.redis.service.ObjectRedisService;
import com.custom.db.redis.templates.JacksonRedisTemplate;
import com.custom.entity.common.HeaderEntity;
import com.custom.entity.common.IllegalRequestEntity;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.request.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @Value("${illegal.request.log}")
    private boolean illegalRequestLog;


    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    IllegalRequestDao illegalRequestDao;

    @Autowired
    private JacksonRedisTemplate jacksonRedisTemplate;

    /**
     * 通用错误拦截器
     *
     */
    @ExceptionHandler(value = Exception.class)
    public JSONObject defaultErrorHandler(HttpServletRequest req, Exception e) {
        String errorPosition = "";
        //如果错误堆栈信息存在
        if (e.getStackTrace().length > 0) {
            StackTraceElement element = e.getStackTrace()[0];
            String fileName = element.getFileName() == null ? "未找到错误文件" : element.getFileName();
            int lineNumber = element.getLineNumber();
            errorPosition = fileName + ":" + lineNumber;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", ErrorEnum.E_400.getErrorCode());
        jsonObject.put("msg", ErrorEnum.E_400.getErrorMsg());
        JSONObject errorObject = new JSONObject();
        errorObject.put("errorLocation", e.toString() + "    错误位置:" + errorPosition);
        // 非生产环境下才打印日志
        jsonObject.put("info", errorObject);
        logger.error("异常", e);
        return jsonObject;
    }

    /**
     * GET/POST请求方法错误的拦截器
     * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
     * 所以定义了这个拦截器
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public JSONObject httpRequestMethodHandler() {
        return Response.error(ErrorEnum.E_500);
    }

    /**
     * 本系统自定义错误的拦截器
     * 拦截到此错误之后,就返回这个类里面的json给前端
     * 常见使用场景是参数校验失败,抛出此错,返回错误信息给前端
     */
    @ExceptionHandler(CommonJsonException.class)
    public JSONObject commonJsonExceptionHandler(CommonJsonException commonJsonException) {
        if(illegalRequestLog){
            // 非法请求，记录进入数据库
            IllegalRequestEntity illegalRequestEntity = new IllegalRequestEntity();
            HeaderEntity headerEntity = Context.getHeader();
            BeanUtil.copyProperties(headerEntity, illegalRequestEntity);
            // 有可能此时没有UID
            illegalRequestEntity.setUid(Context.getUser().getUid());
            illegalRequestEntity.setErrorCode(commonJsonException.getResultJson().getString("code"));
            illegalRequestEntity.setErrorMsg(commonJsonException.getResultJson().getString("msg"));
            illegalRequestDao.add(illegalRequestEntity);

            String uidKey = IllegalRequest.uid();
            String ipKey = IllegalRequest.ip();

            jacksonRedisTemplate.opsForZSet().incrementScore(uidKey, Context.getUser().getUid(), 1);
            jacksonRedisTemplate.opsForZSet().incrementScore(ipKey, illegalRequestEntity.getIp(), 1);

        }
        return commonJsonException.getResultJson();
    }

}
