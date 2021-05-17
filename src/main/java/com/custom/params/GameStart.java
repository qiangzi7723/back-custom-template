package com.custom.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GameStart")
public class GameStart {
    @ApiModelProperty("游戏类型 枚举，默认填写common")
    private String gameType;
}
