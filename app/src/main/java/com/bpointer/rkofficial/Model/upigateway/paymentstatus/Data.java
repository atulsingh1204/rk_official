package com.bpointer.rkofficial.Model.upigateway.paymentstatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("transaction_status")
    @Expose
    private String transactionStatus;
    @SerializedName("remark")
    @Expose
    private String remark;

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
