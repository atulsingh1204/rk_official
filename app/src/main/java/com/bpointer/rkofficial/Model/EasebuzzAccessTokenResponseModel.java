package com.bpointer.rkofficial.Model;

import com.google.gson.annotations.SerializedName;

public class EasebuzzAccessTokenResponseModel {

    private boolean status;
    private String message;
    private EasebuzzPaymentData data;

    // Getters and setters
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EasebuzzPaymentData getData() {
        return data;
    }

    public void setData(EasebuzzPaymentData data) {
        this.data = data;
    }
}
