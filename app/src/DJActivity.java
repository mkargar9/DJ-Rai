package com.example.djrai;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.media.MediaPlayer;

public class DJActivity extends AppCompatActivity {
    ListView lv;
    List<String> list;
    File[] inn;
    String path = Environment.getExternalStorageDirectory().getPath();
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);

        list = new ArrayList<String>();
        //list.add("\"15_Change\" by Lana Del Rey");
        //list.add("\"02_Lust_For_Life_(feat._The_Weekndr\" by Lana Del Rey");
        //list.add("\"04_Cherry\" by Lana Del Rey");
        list.add("\"change\" by Lana Del Rey");
        list.add("\"cherry\" by Lana Del Rey");
        list.add("\"love\" by Lana Del Rey");

        lv = (ListView)findViewById(R.id.song_listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                String songName = name.substring(0, name.indexOf("by")-1).replaceAll("\"", "");
                Toast.makeText(getApplicationContext(), songName, Toast.LENGTH_LONG).show();
                //path = "file:///storage/emulated/0/" + songName +".mp3";
                //path +=  "/" + songName + ".mp3";
                //System.out.println(path);

                MediaPlayer mp = MediaPlayer.create(DJActivity.this, R.raw.change);
                //this should work!!! but it doesn't play anything.....
                try
                {
                    mp.prepare();
                    mp.start();

                }
                catch (IllegalArgumentException e)
                {
                }
                catch (IllegalStateException e)
                {
                }
                catch (IOException e)
                {
                }
            }
        });
    }

}