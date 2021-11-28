package com.sub.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description="登录对象")
public class LoginVo {

    @ApiModelProperty(value = "openid")
    private String openid;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @NotBlank(message = "密码必填")
    @ApiModelProperty(value = "密码")
    private String code;

    @ApiModelProperty(value = "IP")
    private String ip;
}
