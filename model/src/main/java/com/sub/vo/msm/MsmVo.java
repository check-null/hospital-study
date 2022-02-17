package com.sub.vo.msm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "短信实体")
public class MsmVo {

    @ApiModelProperty(value = "mobile")
    private String mobile;

    @ApiModelProperty(value = "短信验证码, 有效时间")
    private String templateParamSet;

    @ApiModelProperty(value = "短信模板")
    private String templateID;

    @ApiModelProperty(value = "短信模板参数")
    private Map<String,Object> param;
}
