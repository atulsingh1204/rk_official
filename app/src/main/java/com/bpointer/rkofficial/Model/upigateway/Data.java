package com.bpointer.rkofficial.Model.upigateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("order_id")
    @Expose
    private Long orderId;
    @SerializedName("payment_url")
    @Expose
    private String paymentUrl;
    @SerializedName("upi_intent")
    @Expose
    private UpiIntent upiIntent;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public UpiIntent getUpiIntent() {
        return upiIntent;
    }

    public void setUpiIntent(UpiIntent upiIntent) {
        this.upiIntent = upiIntent;
    }

}
