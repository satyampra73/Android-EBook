package com.example.androidebookapp.response;

import com.example.androidebookapp.item.AuthorList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AuthorRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ads_param")
    private String ads_param;

    @SerializedName("author_list")
    private List<AuthorList> authorLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getAds_param() {
        return ads_param;
    }

    public List<AuthorList> getAuthorLists() {
        return authorLists;
    }
}
