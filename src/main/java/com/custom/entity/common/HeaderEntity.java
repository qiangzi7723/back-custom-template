package com.custom.entity.common;

import com.custom.util.IPUtil;
import lombok.Data;

@Data
public class HeaderEntity {
    private String api;
    private String method;
    private String ip;
    private String referer;
    private String authorization;
}
