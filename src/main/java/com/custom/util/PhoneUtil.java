package com.custom.util;

import cn.hutool.core.util.StrUtil;

public class PhoneUtil {
    public static String desensitizedPhoneNumber(String phoneNumber){
        if(!StrUtil.isEmpty(phoneNumber)){
            phoneNumber = phoneNumber.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
        }
        return phoneNumber;
    }
}
