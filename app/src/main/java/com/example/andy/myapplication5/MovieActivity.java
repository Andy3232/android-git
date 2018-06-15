package com.example.andy.myapplication5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity {

    private TextView title;
    private ImageView btn_back;
    private ImageButton btn_menu;
    private Button btn_cast;
    private Button btn_desc;
    private Button btn_youtube;
    private ImageButton btn_like;
    private ImageView img_pic;
    private TextView tv_cast;
    private TextView tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        setClick();

    }

    private void setClick() {
        title = (TextView)findViewById(R.id.tv_movie_title);
        btn_back = (ImageView) findViewById(R.id.btn_movie_back);
        btn_menu = (ImageButton) findViewById(R.id.btn_movie_menu);
        btn_cast = (Button) findViewById(R.id.btn_cast_exp);
        btn_desc = (Button) findViewById(R.id.btn_desc_exp);
        btn_youtube = (Button) findViewById(R.id.btn_youtube);
        btn_like = (ImageButton) findViewById(R.id.btn_like);
        img_pic = (ImageView) findViewById(R.id.img_movie_pic);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_cast = (TextView) findViewById(R.id.tv_cast);

        Intent intent = getIntent();
        //title
        String name = intent.getStringExtra("NAME");
        title.setText(name);
        //pic
        String where = intent.getStringExtra("WHERE");
        Bitmap pic = null;
        switch (where) {
            case "INFO":
                pic = InformationActivity.getPassPic();
                break;
            case "SEARCH":
                pic = SearchActivity.getPassPic();
                break;
            case "SORT":
                break;
            case "HOT":
                pic = TrailerActivity.getPassPic();
                break;
        }

        img_pic.setImageBitmap(pic);
        InformationActivity.setPassPic(null);
        //tv_desc
        String desc = intent.getStringExtra("DESC");
        tv_desc.setText(desc);
        //tv_cast
        String cast = intent.getStringExtra("CAST");
        cast = cast.replaceAll(",", "\n");
        tv_cast.setText(cast);

        //btn_desc
        btn_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_desc.getText().toString().equals("+")) {
                    tv_desc.setMaxLines(999);
                    btn_desc.setText("-");
                } else {
                    tv_desc.setMaxLines(0);
                    btn_desc.setText("+");
                }
            }
        });

        //btn_cast
        btn_cast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_cast.getText().toString().equals("+")) {
                    tv_cast.setMaxLines(999);
                    btn_cast.setText("-");
                } else {
                    tv_cast.setMaxLines(0);
                    btn_cast.setText("+");
                }
            }
        });

        //youtube
        String youtube = intent.getStringExtra("YOUTUBE");

        //back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //menu
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(MovieActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                Intent intent = new Intent();
                                intent.setClass(MovieActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_2:

                                break;
                        }
                        return true;
                    }

                });
                popupmenu.show();
            }
        });
    }
}
