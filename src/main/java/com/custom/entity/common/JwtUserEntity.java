package com.custom.entity.common;

import lombok.Data;

@Data
public class JwtUserEntity {
    private Integer id;
    private int uid;
    private String nickname;
    private String avatar;
}
