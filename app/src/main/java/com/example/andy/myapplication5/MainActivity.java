package com.example.andy.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button informationPageBtn = (Button)findViewById(R.id.information_btn);
        informationPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , InformationActivity.class);
                startActivity(intent);
            }
        });

        Button trailerPageBtn = (Button)findViewById(R.id.trailer_btn);
        trailerPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , TrailerActivity.class);
                startActivity(intent);
            }
        });

        Button sortPageBtn = (Button)findViewById(R.id.sort_btn);
        sortPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , SortActivity.class);
                startActivity(intent);
            }
        });

    }
}
