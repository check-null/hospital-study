package com.sub.enums;

public enum PaymentTypeEnum {
    /**
     * 支付宝
     */
    ALI_PAY(1,"支付宝"),
    WEI_XIN(2,"微信" );

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private Integer status ;
    private String comment ;


    PaymentTypeEnum(Integer status, String comment ){
        this.status = status;
        this.comment=comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
