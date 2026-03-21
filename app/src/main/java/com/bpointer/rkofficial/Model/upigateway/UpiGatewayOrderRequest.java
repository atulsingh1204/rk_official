package com.bpointer.rkofficial.Model.upigateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpiGatewayOrderRequest {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("client_txn_id")
    @Expose
    private String clientTxnId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("p_info")
    @Expose
    private String pInfo;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("customer_email")
    @Expose
    private String customerEmail;
    @SerializedName("customer_mobile")
    @Expose
    private String customerMobile;
    @SerializedName("redirect_url")
    @Expose
    private String redirectUrl;
    @SerializedName("udf1")
    @Expose
    private String udf1;
    @SerializedName("udf2")
    @Expose
    private String udf2;
    @SerializedName("udf3")
    @Expose
    private String udf3;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClientTxnId() {
        return clientTxnId;
    }

    public void setClientTxnId(String clientTxnId) {
        this.clientTxnId = clientTxnId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getpInfo() {
        return pInfo;
    }

    public void setpInfo(String pInfo) {
        this.pInfo = pInfo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

}
