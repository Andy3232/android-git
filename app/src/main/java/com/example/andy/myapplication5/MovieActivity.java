package com.example.andy.myapplication5;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MovieActivity  extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

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

    //Youtube
    public static final String API_KEY = "AIzaSyCRzFbaJ9BMMuwPGOhcBRxUcYGU28J0UMA";
    //https://www.youtube.com/watch?v=<video_id>
    public String VIDEO_ID = "OsUr8N7t4zc";
    YouTubePlayer y2;
    private YouTubePlayerView mYoutubePlayerView;

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

        btn_back.setImageResource(R.drawable.ic_back);
        btn_menu.setImageResource(R.drawable.ic_menuwhite);
        btn_like.setImageResource(R.drawable.ic_startempty);

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
        VIDEO_ID = youtube.substring(youtube.indexOf('=') + 1);
        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(MovieActivity.this);
                final View alert_view = inflater.inflate(R.layout.dialog_youtube,null);//alert為另外做給alert用的layout

                //-----------產生輸入視窗--------
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieActivity.this);
                builder.setView(alert_view);
                builder.setCancelable(false);

                //alert.xml上的元件 要用屬於元件的view.才可以不然會FC也就是要加alert_view.findViewById
                mYoutubePlayerView = (YouTubePlayerView) alert_view.findViewById(R.id.youtube);
                mYoutubePlayerView.initialize(API_KEY, MovieActivity.this);

                AlertDialog dialog = builder.create();

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            y2.release();
                            dialog.cancel();
                        }
                        return false;
                    }
                });
                dialog.show();
            }
        });

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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        //Toast.makeText(this, "onInitializationSuccess!", Toast.LENGTH_SHORT).show();
        y2 = youTubePlayer;
        if (youTubePlayer == null) {
            Log.d("CheckPoint", "CheckPoint youtubePlayer == null");
            return;
        }

        if (!wasRestored) {
            Log.d("CheckPoint", "CheckPoint !wasRestored");
            youTubePlayer.cueVideo(VIDEO_ID);
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener(){
                @Override
                public void onFullscreen(boolean arg0) {
                    Toast.makeText(MovieActivity.this, "不支援全螢幕模式", Toast.LENGTH_SHORT).show();
                }});
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        //Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }
}
