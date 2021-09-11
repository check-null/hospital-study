package com.sub.model.hosp;

import com.sub.model.base.BaseMongoEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * Department
 * </p>
 *
 * @author qy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Department")
@Document("Department")
public class Department extends BaseMongoEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 普通索引
     */
    @Indexed
    @ApiModelProperty(value = "医院编号")
    private String hoscode;

    /**
     * 唯一索引
     */
    @ApiModelProperty(value = "科室编号")
    @Indexed(unique = true)
    private String depcode;

    @ApiModelProperty(value = "科室名称")
    private String depname;

    @ApiModelProperty(value = "科室描述")
    private String intro;

    @ApiModelProperty(value = "大科室编号")
    private String bigcode;

    @ApiModelProperty(value = "大科室名称")
    private String bigname;

}
