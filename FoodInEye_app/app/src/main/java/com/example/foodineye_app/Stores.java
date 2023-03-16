package com.example.foodineye_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stores {

    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("notice")
    @Expose
    private String notice;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("img_url")
    @Expose
    private String img_url;
    @SerializedName("m_id")
    @Expose
    private String m_id;

    public String getName() {
        return name;
    }

    @Override
    public String toString()
    {
        return "Stores [schedule = "+schedule+", img_url = "+img_url+", name = "+name+", description = "+description+", _id = "+_id+", notice = "+notice+", status = "+status+", m_id = "+m_id+"]";
    }
}
