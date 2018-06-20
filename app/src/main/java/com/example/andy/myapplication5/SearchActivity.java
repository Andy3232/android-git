package com.example.andy.myapplication5;


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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

public class SearchActivity extends AppCompatActivity {
    private ImageButton btnSearch;
    private EditText etSearch;
    private ImageView btnBack;
    private ImageButton btnMenu;
    private ListView lvInformations;
    private TextView tvNumber;
    private String input = "";
    private static Bitmap passPic;

    List<information> lsinformations = new ArrayList<>();
    private InformationArrayAdapter adapter = null;
    private static final int LIST_MOVIE = 1;
    int num;
    private Boolean exist = true;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            tvNumber.setText("找到" + num + "個結果");
            switch (msg.what) {
                case LIST_MOVIE: {
                    List<information> informations = (List<information>) msg.obj;
                    refreshInformationsList(informations);
                    break;
                }
            }
        }
    };

    private void refreshInformationsList(List<information> informations) {
        adapter.clear();

        adapter.addAll(informations);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setClick();
    }

    private void setClick() {
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnMenu = (ImageButton) findViewById(R.id.btn_search_menu);
        btnBack = (ImageView) findViewById(R.id.btn_search_back);
        etSearch = (EditText) findViewById(R.id.et_search);
        tvNumber = (TextView) findViewById(R.id.tv_number);

        //menu
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(SearchActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                intent = new Intent();
                                intent.setClass(SearchActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                exist = false;
                                finish();
                                break;
                            case R.id.action_2:
                                intent = new Intent();
                                intent.setClass(SearchActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.action_3:
                                android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(SearchActivity.this);
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

        //back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //檢查網路連線
                ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                if (mNetworkInfo != null) {
                    lsinformations = new ArrayList<>();//
                    String pre = input;
                    input = etSearch.getText().toString().trim().toUpperCase();
                    if (!pre.equals(input.trim()) && input.trim().length() > 0) {
                        getInformationsFromFirebase();
                    } else if (input.trim().length() == 0) {
                        Toast.makeText(SearchActivity.this, "請輸入電影名稱!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "沒有網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //et_search listener
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {

                    //檢查網路連線
                    ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                    if (mNetworkInfo != null) {
                        lsinformations = new ArrayList<>();
                        String pre = input;
                        input = etSearch.getText().toString().trim().toUpperCase();
                        if (!pre.equals(input.trim()) && input.trim().length() > 0) {
                            getInformationsFromFirebase();
                        } else if (input.trim().length() == 0) {
                            Toast.makeText(SearchActivity.this, "請輸入電影名稱!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "沒有網路連線", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });


        //list
        lvInformations = (ListView) findViewById(R.id.list_search);
        adapter = new InformationArrayAdapter(this, new ArrayList<information>());
        lvInformations.setAdapter(adapter);

        //set list click
        lvInformations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, MovieActivity.class);
                String name = lsinformations.get(position).getName();
                String desc = lsinformations.get(position).getDesc();
                String youtube = lsinformations.get(position).getYoutube();
                String cast = lsinformations.get(position).getCast();
                String picUrl = lsinformations.get(position).getPicUrl();
                Bitmap pic = lsinformations.get(position).getPic();

                intent.putExtra("WHERE", "SEARCH");
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

    class FirebaseThread extends Thread {

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run() {
            Log.d("GetData", "In");
            num = 0;
            int count = 0;
            exist = true;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                DataSnapshot dsName = ds.child("NAME");
                String name = (String) dsName.getValue();

                if (exist && name.indexOf(input) > -1) {
                    count++;
                    num++;
                    //get database data
                    DataSnapshot dsDesc = ds.child("DESC");
                    DataSnapshot dsPic = ds.child("PIC");
                    DataSnapshot dsCast = ds.child("CAST");
                    DataSnapshot dsYoutube = ds.child("YOUTUBE");
                    DataSnapshot dsSort = ds.child("SORT");
                    DataSnapshot dsDate = ds.child("DATE");
                    DataSnapshot dsNow = ds.child("NOW");

                    //make each string
                    String desc = (String) dsDesc.getValue();
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
                    ainformation.setDesc(desc);
                    ainformation.setName(name);
                    ainformation.setPic(pic);
                    ainformation.setSort(sort);
                    ainformation.setYoutube(youtube);
                    ainformation.setNow(now);
                    ainformation.setPicUrl(picUrl);

                    lsinformations.add(ainformation);
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
                new SearchActivity.FirebaseThread(dataSnapshot).start();
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
            tvName.setText(item.getName());

            switch (item.getNow()) {
                case "0":
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

            TextView tvSort = (TextView) itemlayout.findViewById(R.id.tv_sort);
            tvSort.setText(item.getSort());
            ImageView ivmovie = (ImageView) itemlayout.findViewById(R.id.iv_movie);
            ivmovie.setImageBitmap(item.getPic());
            return itemlayout;
        }
    }

    @Override
    protected void onDestroy() {
        exist = false;
        adapter.clear();
        lsinformations.clear();
        super.onDestroy();
    }

    public static Bitmap getPassPic() {
        return passPic;
    }

    public static void setPassPic(Bitmap passPic) {
        SearchActivity.passPic = passPic;
    }
}
