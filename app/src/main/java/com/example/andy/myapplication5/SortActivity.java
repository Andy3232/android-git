package com.example.andy.myapplication5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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

import java.util.ArrayList;
import java.util.List;

public class SortActivity extends AppCompatActivity {

    private ListView lv_sort;
    private ImageView btn_back;
    private ImageButton btn_menu;
    private ArrayList<String> sortList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        click();
    }

    private void click() {
        lv_sort = (ListView) findViewById(R.id.lv_sort);
        btn_back = (ImageView) findViewById(R.id.btn_sort_back);
        btn_menu = (ImageButton) findViewById(R.id.btn_sort_menu);

        //menu
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupmenu = new PopupMenu(SortActivity.this, v);

                popupmenu.getMenuInflater().inflate(R.menu.menu, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_1:
                                Intent intent = new Intent();
                                intent.setClass(SortActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_2:
                                intent = new Intent();
                                intent.setClass(SortActivity.this, LikeActivity.class);
                                startActivity(intent);
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

        //set list
        sortList = new ArrayList<String>();
        String[] sortAll = {"劇情", "勵志","動作","冒險","科幻","戰爭","恐怖","懸疑","驚悚","愛情","喜劇","歷史"};
        for (String temp: sortAll) {
            sortList.add(temp);
        }
        SortArrayAdapter adapter = new SortArrayAdapter(this,sortList);
        lv_sort.setAdapter(adapter);

        //list Onclick
        lv_sort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(SortActivity.this, SortedMovie.class);
                intent.putExtra("SORT", sortList.get(position));
                startActivity(intent);
            }
        });
    }

    class SortArrayAdapter extends ArrayAdapter<String> {
        Context context;

        public SortArrayAdapter(Context context, List<String> items) {
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.item_sort, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }

            String item = (String) getItem(position);
            TextView tvSortItem = (TextView) itemlayout.findViewById(R.id.tv_sort_item);
            tvSortItem.setText(sortList.get(position));

            return itemlayout;
        }
    }
}
