package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.foodineye_app.R;
import com.lakue.pagingbutton.LakuePagingButton;
import com.lakue.pagingbutton.OnPageSelectListener;

public class OrderHistoryActivity extends AppCompatActivity {

    LakuePagingButton lpb_buttonlist;
    int page = 1;
    int max_page = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lpb_buttonlist = findViewById(R.id.lakuePagingButton);

        lpb_buttonlist.setPageItemCount(4);
        lpb_buttonlist.addBottomPageButton(max_page,1);

        lpb_buttonlist.setOnPageSelectListener(new OnPageSelectListener() {
            //BeforeButton Click
            @Override
            public void onPageBefore(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
                Toast.makeText(OrderHistoryActivity.this, ""+now_page, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageCenter(int now_page) {
                Toast.makeText(OrderHistoryActivity.this, ""+now_page, Toast.LENGTH_SHORT).show();
                //  lpb_buttonlist.addBottomPageButton(max_page,page);
            }

            //NextButton Click
            @Override
            public void onPageNext(int now_page) {
                Toast.makeText(OrderHistoryActivity.this, ""+now_page, Toast.LENGTH_SHORT).show();
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
            }
        });




    }
}