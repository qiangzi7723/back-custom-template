package com.custom.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GameRank")
public class GameRank {
    @ApiModelProperty("页码，从0开始")
    private int pageOrder;

    @ApiModelProperty("每页数量")
    private int pageNum;

    @ApiModelProperty("游戏类型 填写common即可")
    private String gameType;
}
