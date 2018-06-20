package com.example.andy.myapplication5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends AppCompatActivity {

    List<information> lsinformations = new ArrayList<>();
    private InformationArrayAdapter adapter = null;
    private static final int LIST_MOVIE = 1;

    private ImageView btnBack;
    private ImageButton btnMenu;
    private ListView lvInformations;
    private Boolean exist = true;
    private static Bitmap passPic;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_MOVIE: {
                    List<information> informations = (List<information>) msg.obj;
                    refreshHotelList(informations);
                    break;
                }
            }
        }
    };

    private void refreshHotelList(List<information> informations) {
        adapter.clear();

        adapter.addAll(informations);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //檢查網路連線
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo != null) {
            Toast.makeText(InformationActivity.this, "載入中", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(InformationActivity.this, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }

        setClick();
        getInformationsFromFirebase();
    }

    class FirebaseThread extends Thread {

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run() {
            Log.d("GetData", "In");
            int count = 0;
            exist = true;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                count++;
                if (exist) {
                    //get database date
                    DataSnapshot dsDesc = ds.child("DESC");
                    DataSnapshot dsName = ds.child("NAME");
                    DataSnapshot dsPic = ds.child("PIC");
                    DataSnapshot dsCast = ds.child("CAST");
                    DataSnapshot dsYoutube = ds.child("YOUTUBE");
                    DataSnapshot dsSort = ds.child("SORT");
                    DataSnapshot dsDate = ds.child("DATE");
                    DataSnapshot dsNow = ds.child("NOW");

                    //make each string
                    String desc = (String) dsDesc.getValue();
                    String name = (String) dsName.getValue();
                    String picUrl = (String) dsPic.getValue();
                    String cast = (String) dsCast.getValue();
                    String youtube = (String) dsYoutube.getValue();
                    String sort = (String) dsSort.getValue();
                    String date = (String) dsDate.getValue();
                    String now = (String) dsNow.getValue();
                    Log.d("NAME", name);

                    //turn bitmap
                    Bitmap pic = getImgBitmap(picUrl);

                    //create a information
                    information ainformation = new information();
                    ainformation.setCast(cast);
                    ainformation.setDate(date);
                    ainformation.setPic(pic);
                    ainformation.setDesc(desc);
                    ainformation.setName(name);
                    ainformation.setSort(sort);
                    ainformation.setYoutube(youtube);
                    ainformation.setNow(now);
                    ainformation.setPicUrl(picUrl);

                    lsinformations.add(ainformation);

                } else {
                    break;
                }
                if (count % 2 == 0) {
                    Message msg = new Message();
                    msg.what = LIST_MOVIE;
                    msg.obj = lsinformations;
                    handler.sendMessage(msg);
                }
            }
            if (count % 2 != 0) {
                Message msg = new Message();
                msg.what = LIST_MOVIE;
                msg.obj = lsinformations;
                handler.sendMessage(msg);
            }
        }
    }

    private void getInformationsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot value = dataSnapshot;
                new FirebaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Information", databaseError.getMessage());
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

    private void setClick() {
        btnMenu = (ImageButton) findViewById(R.id.btn_movielist_menu);
        btnBack = (ImageView) findViewById(R.id.btn_movielist_back);

        //back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //menu
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(InformationActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                intent = new Intent();
                                intent.setClass(InformationActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_2:
                                intent = new Intent();
                                intent.setClass(InformationActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.action_3:
                                AlertDialog.Builder ad = new AlertDialog.Builder(InformationActivity.this);
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

        //list
        lvInformations = (ListView) findViewById(R.id.listview_movie);
        adapter = new InformationArrayAdapter(this, new ArrayList<information>());
        lvInformations.setAdapter(adapter);

        //set list click
        lvInformations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(InformationActivity.this, MovieActivity.class);
                String name = lsinformations.get(position).getName();
                String desc = lsinformations.get(position).getDesc();
                String youtube = lsinformations.get(position).getYoutube();
                String cast = lsinformations.get(position).getCast();
                String picUrl = lsinformations.get(position).getPicUrl();
                Bitmap pic = lsinformations.get(position).getPic();

                intent.putExtra("WHERE", "INFO");
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

    @Override
    protected void onPause() {
        Log.d("PAUSE","123");
        exist = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("DEST","Info");
        exist = false;
        lsinformations.clear();
        adapter.clear();
        super.onDestroy();
    }

    public static Bitmap getPassPic() {
        return passPic;
    }

    public static void setPassPic(Bitmap passPic) {
        InformationActivity.passPic = passPic;
    }

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
                itemlayout = (LinearLayout) inflater.inflate(R.layout.information_layout, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }

            information item = (information) getItem(position);
            TextView tvName = (TextView) itemlayout.findViewById(R.id.tv_name);
            TextView tvDate = (TextView) itemlayout.findViewById(R.id.tv_date);
            TextView tvSort = (TextView) itemlayout.findViewById(R.id.tv_sort);

            tvName.setText(item.getName());
            switch(item.getNow()) {
                case"0":
                    tvDate.setText("上映日期: " + item.getDate());
                    tvDate.setTextColor(0xFF000000);
                    break;
                case "1":
                    tvDate.setText("現正上映中");
                    tvDate.setTextColor(0xFFFF0000);
                    break;
                default:
                    tvDate.setText("上映日期: " + item.getDate());
            }

            tvSort.setText(item.getSort());


            ImageView ivmovie = (ImageView) itemlayout.findViewById(R.id.iv_movie);
            ivmovie.setImageBitmap(item.getPic());
            return itemlayout;
        }
    }
}