package com.example.foodineye_app.activity;

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
    @SerializedName("m_id")
    @Expose
    private String m_id;
    @SerializedName("num")
    @Expose
    private int s_num;

    // status == 2 -> Close, status == 1 -> Open
    public boolean isOpen(){
        if (status == 2){
            return true;
        }else return false;
    }

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

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public int getS_num() {
        return s_num;
    }

    public void setS_num(int s_num) {
        this.s_num = s_num;
    }

    @Override
    public String toString()
    {
        return "Stores [schedule = "+schedule+", name = "+name+", description = "+desc+", _id = "+_id+", notice = "+notice+", status = "+status+", m_id = "+m_id+"]";
    }
}
