package com.example.foodineye_app.data;


//가게의 s_num과 그 가게의 해당하는 f_num의 배열이 존재
public class MetaInfoData {

    private int sNum;
    private int[] fNumList;

    public MetaInfoData(int sNum, int[] fNumList) {
        this.sNum = sNum;
        this.fNumList = fNumList;
    }

    public int getsNum() {
        return sNum;
    }

    public void setsNum(int sNum) {
        this.sNum = sNum;
    }

    public int[] getfNumList() {
        return fNumList;
    }

    public void setfNumList(int[] fNumList) {
        this.fNumList = fNumList;
    }
}
