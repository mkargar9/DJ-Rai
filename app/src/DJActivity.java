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
    Boolean setPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);

        list = new ArrayList<String>();
        list.add("\"change\" by Lana Del Rey" + "\n " +
            MainActivity.getCurrentSkipVotes() + " votes to skip");
        list.add("\"cherry\" by Lana Del Rey" + "\n " +
                MainActivity.getCurrentSkipVotes() + " votes to skip");
        list.add("\"love\" by Lana Del Rey" + "\n " +
                MainActivity.getCurrentSkipVotes() + " votes to skip");

        lv = (ListView)findViewById(R.id.song_listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                System.out.println(name);
                String songName = name.substring(1, name.indexOf("by")-2);
                Toast.makeText(getApplicationContext(), songName, Toast.LENGTH_LONG).show();
                System.out.println(songName);
                if (!setPlaying)
                {
                    playSound(songName);
                    setPlaying = true;
                }
                else
                {
                    stopPlayer();
                    setPlaying = false;
                    playSound(songName);
                }
            }
        });

    }

    public void playSound(String SongName)
    {
        if (mp == null)
        {
            if ( SongName.equals("change") )
            {
                mp = MediaPlayer.create(DJActivity.this, R.raw.change);

            }
            else if (SongName.equals("cherry"))
            {
                mp = MediaPlayer.create(DJActivity.this, R.raw.cherry);

            }
            else
            {
                mp = MediaPlayer.create(DJActivity.this, R.raw.love);
            }
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp2)
                {
                    setPlaying = false;
                    stopPlayer();
                }
            });
        }
        mp.start();
    }

    private void stopPlayer() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

}