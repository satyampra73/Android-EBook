package com.example.androidebookapp.response;

import com.example.androidebookapp.item.CategoryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CatRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ads_param")
    private String ads_param;

    @SerializedName("category_list")
    private List<CategoryList> categoryLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getAds_param() {
        return ads_param;
    }

    public List<CategoryList> getCategoryLists() {
        return categoryLists;
    }
}
