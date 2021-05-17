package com.custom.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GameCommit")
public class GameCommit {
    @ApiModelProperty("游戏类型 填写common即可")
    private String gameType;

    @ApiModelProperty("分数")
    private int score;

    @ApiModelProperty("开始游戏的秘钥")
    private String token;

    @ApiModelProperty("开始游戏时返回的recordId")
    private int recordId;
}
