package com.bpointer.rkofficial.Model.Response;

import com.google.gson.annotations.SerializedName;

public class RazorpayOrderResponseModel {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private RazorpayOrderData data;

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

    public RazorpayOrderData getData() {
        return data;
    }

    public void setData(RazorpayOrderData data) {
        this.data = data;
    }

    public static class RazorpayOrderData {

        @SerializedName("razorpay_order_id")
        private String razorpayOrderId;

        @SerializedName("amount")
        private int amount;

        @SerializedName("currency")
        private String currency;

        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public void setRazorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}

