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
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("notice")
    @Expose
    private String notice;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("img_src")
    @Expose
    private String img_url;
    @SerializedName("m_id")
    @Expose
    private String m_id;

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    @Override
    public String toString()
    {
        return "Stores [schedule = "+schedule+", img_url = "+img_url+", name = "+name+", description = "+desc+", _id = "+_id+", notice = "+notice+", status = "+status+", m_id = "+m_id+"]";
    }
}
