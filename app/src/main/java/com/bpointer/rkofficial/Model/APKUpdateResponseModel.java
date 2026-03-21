package com.bpointer.rkofficial.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class APKUpdateResponseModel {

    @SerializedName("latestVersion")
    @Expose
    private String latestVersion;


    @SerializedName("latestVersionCode")
    @Expose
    private String latestVersionCode;


    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("releaseNotes")
    @Expose
    private List<String> releaseNotes;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(String latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(List<String> releaseNotes) {
        this.releaseNotes = releaseNotes;
    }
}
