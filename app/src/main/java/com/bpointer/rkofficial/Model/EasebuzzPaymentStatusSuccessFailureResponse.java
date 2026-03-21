package com.bpointer.rkofficial.Model;

public class EasebuzzPaymentStatusSuccessFailureResponse {
    private boolean status;
    private String message;
    private PaymentStatusData data;

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

    public PaymentStatusData getData() {
        return data;
    }

    public void setData(PaymentStatusData data) {
        this.data = data;
    }
}
