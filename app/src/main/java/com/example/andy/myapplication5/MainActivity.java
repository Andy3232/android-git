package com.example.andy.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //list OnClick
        setOnClick();

        //set Menu
        ImageButton btn = (ImageButton) findViewById(R.id.btn_menu);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(MainActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.main_menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_2:
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.action_3:
                                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                                ad.setTitle("關於程式");
                                ad.setMessage("程式名稱 : FeatureMovies\n作者 : 莊文明、徐弘欣、邱泓嶧");


                                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                };

                                ad.setPositiveButton("OK",listener);
                                ad.show();
                                break;


                        }
                        return true;
                    }

                });
                popupmenu.show();
            }
        });
    }

    //four click
    private View.OnClickListener movieListClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , InformationActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener searchClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener nowHotClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , TrailerActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener sortClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , SortActivity.class);
            startActivity(intent);
        }
    };

    private void setOnClick() {
        //set movielist onclick
        ImageView img_movie = (ImageView) findViewById(R.id.img_movielist);
        TextView tv_movie = (TextView) findViewById(R.id.tv_movielist);
        img_movie.setOnClickListener(movieListClick);
        tv_movie.setOnClickListener(movieListClick);

        //set search onclick
        ImageView img_search = (ImageView)findViewById(R.id.img_search);
        TextView tv_search = (TextView) findViewById(R.id.tv_search);
        img_search.setOnClickListener(searchClick);
        tv_search.setOnClickListener(searchClick);

        //set nowhot onclick
        ImageView img_nowhot = (ImageView) findViewById(R.id.img_hot);
        TextView tv_nowhot = (TextView) findViewById(R.id.tv_hot);
        img_nowhot.setOnClickListener(nowHotClick);
        tv_nowhot.setOnClickListener(nowHotClick);

        //set sort onclkck
        ImageView img_sort = (ImageView) findViewById(R.id.img_sort);
        TextView tv_sort = (TextView) findViewById(R.id.tv_sort);
        img_sort.setOnClickListener(sortClick);
        tv_sort.setOnClickListener(sortClick);
    }
}