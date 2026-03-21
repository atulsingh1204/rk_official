package com.bpointer.rkofficial.Model;

public class PaymentStatusData {
    private String txnid;
    private int amount;
    private String email;
    private String phone;
    private String easepayid;
    private String status;

    // Getters and setters
    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEasepayid() {
        return easepayid;
    }

    public void setEasepayid(String easepayid) {
        this.easepayid = easepayid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
