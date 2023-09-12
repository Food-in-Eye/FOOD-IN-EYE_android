package com.example.foodineye_app.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.foodineye_app.R;

public class CustomLoading extends Dialog {

    public CustomLoading(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_process); // 커스텀 로딩 레이아웃 설정
        setCancelable(false); // 사용자가 닫을 수 없도록 설정
    }
}