package com.custom.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Status")
public class BaseList {
        @ApiModelProperty("活动ID")
        private int activityId;
}
