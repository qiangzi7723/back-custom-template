package com.custom.entity.common;

import lombok.Data;

@Data
public class IllegalRequestEntity {
    private int uid;
    private String ip;
    private String api;
    private String errorCode;
    private String errorMsg;
}
