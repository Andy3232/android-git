package com.example.andy.myapplication5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrailerActivity extends AppCompatActivity {

    private TrailerArrayAdapter adapter = null;

    private static final int LIST_MOVIE1 = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_MOVIE1: {
                    List<trailer> trailers = (List<trailer>) msg.obj;
                    refreshHotelList(trailers);
                    break;
                }
            }
        }
    };

    private void refreshHotelList(List<trailer> trailers) {
        adapter.clear();

        adapter.addAll(trailers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        ListView lvTrailers = (ListView) findViewById(R.id.listview_movie);


        adapter = new TrailerArrayAdapter(this, new ArrayList<trailer>());
        lvTrailers.setAdapter(adapter);

        getTrailersFromFirebase();

    }

    class FirebaseThread extends Thread {

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run() {
            List<trailer> lstrailers = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                DataSnapshot dsUrl = ds.child("Description");

                String Url = (String) dsUrl.getValue();
                DataSnapshot dsImg = ds.child("Picture1");
                String imgUrl = (String) dsImg.getValue();
                Bitmap movieImg = getImgBitmap(imgUrl);

                trailer atrailer = new trailer();
                atrailer.setUrl(Url);
                atrailer.setImgUrl(movieImg);
                lstrailers.add(atrailer);
                Log.v("Trailer", imgUrl);
            }
            Message msg = new Message();
            msg.what = LIST_MOVIE1;
            msg.obj = lstrailers;
            handler.sendMessage(msg);
        }
    }

    private void getTrailersFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot value = dataSnapshot;
                new TrailerActivity.FirebaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Trailer", databaseError.getMessage());
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


    class TrailerArrayAdapter extends ArrayAdapter<trailer> {
        Context context;

        public TrailerArrayAdapter(Context context, List<trailer> items) {
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.trailer_layout, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }
            final trailer item = (trailer) getItem(position);
            ImageView ivmovie = (ImageView) itemlayout.findViewById(R.id.iv_movie1);
            ivmovie.setImageBitmap(item.getImgUrl());
            Button trailerPageBtn = (Button)itemlayout.findViewById(R.id.trailer_btn1);
            trailerPageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.youtube.com"));
                    startActivity(i);
                }
            });

            return itemlayout;
        }
    }
}
