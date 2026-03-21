package com.bpointer.rkofficial.Model.upigateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpiIntent {

    @SerializedName("bhim_link")
    @Expose
    private String bhimLink;
    @SerializedName("phonepe_link")
    @Expose
    private String phonepeLink;
    @SerializedName("paytm_link")
    @Expose
    private String paytmLink;
    @SerializedName("gpay_link")
    @Expose
    private String gpayLink;

    public String getBhimLink() {
        return bhimLink;
    }

    public void setBhimLink(String bhimLink) {
        this.bhimLink = bhimLink;
    }

    public String getPhonepeLink() {
        return phonepeLink;
    }

    public void setPhonepeLink(String phonepeLink) {
        this.phonepeLink = phonepeLink;
    }

    public String getPaytmLink() {
        return paytmLink;
    }

    public void setPaytmLink(String paytmLink) {
        this.paytmLink = paytmLink;
    }

    public String getGpayLink() {
        return gpayLink;
    }

    public void setGpayLink(String gpayLink) {
        this.gpayLink = gpayLink;
    }

}
