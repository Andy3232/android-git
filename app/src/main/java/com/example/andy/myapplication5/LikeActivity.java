package com.example.andy.myapplication5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {

    //view
    GridView gv_like;
    ImageView btn_back;
    ImageButton btn_menu;
    TextView tv_non_like;

    List<information> lsinformations = new ArrayList<>();
    private InformationArrayAdapter adapter = null;
    private static Bitmap passPic = null;
    private static final int LIST_LIKE = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_LIKE: {
                    List<information> informations = (List<information>) msg.obj;
                    for (information info: informations) {
                        Log.d("Name456",info.getName());
                    }
                    refreshInformationsList();
                    Log.d("Handler", "456");
                    break;
                }
            }
        }
    };

    private void refreshInformationsList() {
        gv_like.setAdapter(null);
        gv_like.setAdapter(new InformationArrayAdapter(this, lsinformations));
        /*adapter.clear();
        adapter.addAll(lsinformations);*/
        Log.d("Refresh", "789");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        //setClick();
    }

    protected void onStart() {
        super.onStart();
        lsinformations = new ArrayList<>();
        adapter = null;
        passPic = null;
        setClick();
    }

    private void setClick() {
        gv_like = (GridView) findViewById(R.id.gv_like_list);
        btn_back = (ImageView) findViewById(R.id.btn_like_back);
        btn_menu = (ImageButton) findViewById(R.id.btn_like_menu);
        tv_non_like = (TextView) findViewById(R.id.tv_non_like);
        tv_non_like.setText("");

        //menu
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(LikeActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.like_menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                Intent intent = new Intent();
                                intent.setClass(LikeActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_3:

                                break;
                        }
                        return true;
                    }

                });
                popupmenu.show();
            }
        });

        //back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get db data
        MyDBHelper dbHelper = new MyDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "select * from " + MyDBHelper.getDatabaseTable();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        //new LikeActivity.LikeThread(c).start();
        //set gv_like
        adapter = new InformationArrayAdapter(this, lsinformations);
        gv_like.setNumColumns(3);
        gv_like.setAdapter(adapter);

        if (c.getCount() == 0) {
            tv_non_like.setText("目前沒有收藏");
        } else {
            //檢查網路連線
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (mNetworkInfo != null) {
                Toast.makeText(LikeActivity.this, "載入中", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LikeActivity.this, "沒有網路連線", Toast.LENGTH_SHORT).show();
            }
            for (int i = 0; i < c.getCount(); i++) {
                String name = c.getString(0);
                String picUrl = c.getString(1);
                String cast = c.getString(2);
                String desc = c.getString(3);
                String youtube = c.getString(4);
                //Bitmap pic = getImgBitmap(picUrl);
                information anInfo = new information();
                anInfo.setPicUrl(picUrl);
                anInfo.setYoutube(youtube);
                anInfo.setName(name);
                anInfo.setDesc(desc);
                anInfo.setCast(cast);
                //anInfo.setPic(pic);
                lsinformations.add(anInfo);
                Log.d("URL", anInfo.getPicUrl());
                c.moveToNext();
            }
            new LikeActivity.LikeThread(lsinformations).start();
        }




        //gv_click
        gv_like.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(LikeActivity.this, MovieActivity.class);
                String name = lsinformations.get(position).getName();
                String desc = lsinformations.get(position).getDesc();
                String youtube = lsinformations.get(position).getYoutube();
                String cast = lsinformations.get(position).getCast();
                String picUrl = lsinformations.get(position).getPicUrl();
                Bitmap pic = lsinformations.get(position).getPic();

                intent.putExtra("WHERE", "LIKE");
                intent.putExtra("DESC", desc);
                intent.putExtra("YOUTUBE", youtube);
                intent.putExtra("CAST", cast);
                intent.putExtra("PICURL", picUrl);

                //intent.putExtra("PIC", pic);
                passPic = pic;
                intent.putExtra("NAME", name);
                Log.d("Click", String.valueOf(position));
                startActivity(intent);
            }
        });
    }

    private Bitmap getImgBitmap(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getPassPic() {
        return passPic;
    }

    public static void setPassPic(Bitmap passPic) {
        LikeActivity.passPic = passPic;
    }

    //adapter
    class InformationArrayAdapter extends ArrayAdapter<information> {
        Context context;

        public InformationArrayAdapter(Context context, List<information> items) {
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;

            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.gv_like, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }

            information item = (information) getItem(position);
            TextView tvName = (TextView) itemlayout.findViewById(R.id.tv_like_name);
            tvName.setText(item.getName());

            ImageView imgPic = (ImageView) itemlayout.findViewById(R.id.img_like_pic);
            imgPic.setImageBitmap(item.getPic());
            return itemlayout;
        }
    }

    class LikeThread extends Thread {
        List<information> info;

        public LikeThread(List<information> info) {
            this.info = info;
        }

        public void run() {
            int i = 0;
            for (information temp: info) {
                Log.d("Name123", temp.getName());
                temp.setPic(getImgBitmap(temp.getPicUrl()));
                lsinformations.set(i, temp);
                i++;
            }
            Message msg = new Message();
            msg.what = LIST_LIKE;
            msg.obj = lsinformations;
            handler.sendMessage(msg);
            Log.d("Message", "123");
        }
    }
}
