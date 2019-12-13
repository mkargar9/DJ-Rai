package com.example.djrai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DJActivity extends AppCompatActivity {
    ListView lv;
    List<String> list;
    File file;
    File[] inn;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);

        list = new ArrayList<String>();
        list.add("\"Despacito\" by Luis Fonsi");
        list.add("\"Video Games\" by Lana Del Rey");
        list.add("\"Up All Night\" by One Direction");

        lv = (ListView)findViewById(R.id.song_listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
            }
        });
    }
}