package com.example.andy.myapplication5;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    //view
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
    private Boolean ifLike = false;
    //db
    MyDBHelper dbHelper;
    SQLiteDatabase db;
    String name;
    String picUrl;
    String desc;
    String cast;
    String youtube;
    //Youtube
    public static final String API_KEY = "AIzaSyCRzFbaJ9BMMuwPGOhcBRxUcYGU28J0UMA";
    public String VIDEO_ID = "";
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


        dbHelper = new MyDBHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        //title
        name = intent.getStringExtra("NAME");
        title.setText(name);
        //set star
        String sql = "SELECT * FROM " + MyDBHelper.getDatabaseTable() +" WHERE TRIM(mName) = '"+name.trim()+"'";
        Cursor c = db.rawQuery(sql, null);

        if (c.getCount() == 0) {
            btn_like.setImageResource(R.drawable.ic_startempty);
            ifLike = false;
        } else {
            btn_like.setImageResource(R.drawable.ic_starfull);
            ifLike = true;
        }

        //pic
        String where = intent.getStringExtra("WHERE");
        Bitmap pic = null;
        switch (where) {
            case "INFO":
                pic = InformationActivity.getPassPic();
                InformationActivity.setPassPic(null);
                break;
            case "SEARCH":
                pic = SearchActivity.getPassPic();
                SearchActivity.setPassPic(null);
                break;
            case "SORT":
                pic = SortedMovie.getPassPic();
                SortedMovie.setPassPic(null);
                break;
            case "HOT":
                pic = TrailerActivity.getPassPic();
                TrailerActivity.setPassPic(null);
                break;
            case "LIKE":
                pic = LikeActivity.getPassPic();
                LikeActivity.setPassPic(null);
                break;
        }

        picUrl = intent.getStringExtra("PICURL");
        cast = intent.getStringExtra("CAST");
        desc = intent.getStringExtra("DESC");
        //set item
        cast = cast.replaceAll(",", "\n");
        img_pic.setImageBitmap(pic);
        tv_desc.setText(desc);
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
        youtube = intent.getStringExtra("YOUTUBE");
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
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                intent = new Intent();
                                intent.setClass(MovieActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_2:
                                intent = new Intent();
                                intent.setClass(MovieActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.action_3:
                                android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(MovieActivity.this);
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

        //btn_like
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifLike) {
                    //delete db
                    ifLike = false;
                    btn_like.setImageResource(R.drawable.ic_startempty);
                    db.delete(MyDBHelper.getDatabaseTable(), "mName = ?",new String[] {name});
                    Toast.makeText(MovieActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                } else {
                    //insert db
                    ifLike = true;
                    btn_like.setImageResource(R.drawable.ic_starfull);
                    ContentValues cv = new ContentValues();
                    cv.put("mName", name);
                    cv.put("mPicUrl", picUrl);
                    cv.put("mCast", cast);
                    cv.put("mDesc", desc);
                    cv.put("mYoutube", youtube);
                    db.insert(MyDBHelper.getDatabaseTable(), null, cv);
                    Toast.makeText(MovieActivity.this,"已新增收藏",Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
