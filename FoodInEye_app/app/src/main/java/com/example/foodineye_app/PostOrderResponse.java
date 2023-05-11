package com.example.foodineye_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostOrderResponse {

    @SerializedName("request")
    @Expose
    public String request;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("response")
    @Expose
    public List<Response> response;
    @SerializedName("history_id")
    @Expose
    public String history_id;

    public String getRequest() {    return request;   }

    public void setRequest(String request) {   this.request = request;   }

    public String getStatus() {   return status;    }

    public void setStatus(String status) {     this.status = status;   }

    public List<Response> getResponse() {    return response;   }

    public void setResponse(List<Response> response) {     this.response = response;   }

    public String getHistory_id() {   return history_id;   }

    public void setHistory_id(String history_id) {    this.history_id = history_id;   }

    //response
    public class Response{
        @SerializedName("s_id")
        @Expose
        public String s_id;
        @SerializedName("o_id")
        @Expose
        public String o_id;

        public String getS_id() {  return s_id;  }

        public void setS_id(String s_id) { this.s_id = s_id;  }

        public String getO_id() {   return o_id;   }

        public void setO_id(String o_id) { this.o_id = o_id;  }
    }
}
