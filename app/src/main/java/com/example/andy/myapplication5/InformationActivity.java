package com.example.andy.myapplication5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class InformationActivity extends AppCompatActivity {


    private InformationArrayAdapter adapter = null;

    private static final int LIST_MOVIE = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_MOVIE: {
                    List<information> informations = (List<information>)msg.obj;
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

        ListView lvInformations =(ListView)findViewById(R.id.listview_movie);


        adapter = new InformationArrayAdapter(this, new ArrayList<information>());
        lvInformations.setAdapter(adapter);

        getInformationsFromFirebase();
    }
    class FirebaseThread extends Thread {

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run(){
            List<information> lsinformations = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                DataSnapshot dsDescription = ds.child("Description");

                String Description = (String)dsDescription.getValue();

                DataSnapshot dsImg = ds.child("Picture1");
                String imgUrl = (String) dsImg.getValue();
                Bitmap movieImg = getImgBitmap(imgUrl);

                information ainformation = new information();
                ainformation.setDescription(Description);
                ainformation.setImgUrl(movieImg);
                lsinformations.add(ainformation);
                Log.v("Movie", imgUrl);
            }
            Message msg = new Message();
            msg.what = LIST_MOVIE;
            msg.obj = lsinformations;
            handler.sendMessage(msg);
        }
    }
    private void getInformationsFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                DataSnapshot value = dataSnapshot;
                new FirebaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
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

        public InformationArrayAdapter(Context context, List<information> items){
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout)inflater.inflate(R.layout.information_layout, null);
            } else {
                itemlayout = (LinearLayout)convertView;
            }
            information item = (information) getItem(position);
            TextView tvDescription = (TextView)itemlayout.findViewById(R.id.tv_Description);
            tvDescription.setText(item.getDescription());
            ImageView ivmovie = (ImageView) itemlayout.findViewById(R.id.iv_movie);
            ivmovie.setImageBitmap(item.getImgUrl());
            return itemlayout;
        }
    }
}