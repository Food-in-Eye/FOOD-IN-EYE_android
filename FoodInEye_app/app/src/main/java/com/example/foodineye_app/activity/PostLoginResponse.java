package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostLoginResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("response")
    @Expose
    public Token tokens;

    @SerializedName("message")
    @Expose
    public String errormessage;

    @Override
    public String toString() {
        return "PostLoginResponse{" +
                "request='" + request + '\'' +
                ", status='" + status + '\'' +
                ", tokens=" + tokens +
                ", errormessage='" + errormessage + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public class Token{

        @SerializedName("user_id")
        @Expose
        public String user_id; //고유한 id

        @SerializedName("token_type")
        @Expose
        public String token_type; //고유한 id

        @SerializedName("A_Token")
        @Expose
        public String AT; //access token

        @SerializedName("R_Token")
        @Expose
        public String RT; //refresh token

        @Override
        public String toString() {
            return "Token{" +
                    "user_id='" + user_id + '\'' +
                    ", token_type='" + token_type + '\'' +
                    ", AT='" + AT + '\'' +
                    ", RT='" + RT + '\'' +
                    '}';
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getAT() {
            return AT;
        }

        public void setAT(String AT) {
            this.AT = AT;
        }

        public String getRT() {
            return RT;
        }

        public void setRT(String RT) {
            this.RT = RT;
        }
    }
}
