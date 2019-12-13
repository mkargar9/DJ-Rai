package com.example.djrai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class DJActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
    MyCustomAdapter mAdapter;
    File directory;
    String path;
    MediaPlayer mp;
    Boolean setPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);

        mAdapter = new MyCustomAdapter();

        lv = (ListView)findViewById(R.id.song_listview);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> song = mAdapter.getItem(position);
                String songName = (String)song.get("songName");

                if (!setPlaying) {
                    playSong(songName);
                    setPlaying = true;
                }
                else {
                    stopPlayer();
                    setPlaying = false;
                    playSong(songName);
                }
            }
        });

        // hard code location because android emulator places files here by default
        path = "/sdcard/Download/";
        directory = new File(path);
        // filter files in that folder to only the ones that end with .mp3
        File[] songFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getPath().endsWith(".mp3"));
            }
        });

        for (int i = 0; i < songFiles.length; i++) {
            HashMap<String, Object> song = new HashMap<>();
            String s = songFiles[i].getName();

            song.put("songName", s.substring(0, s.lastIndexOf('.')));
            song.put("likes", 0);
            song.put("dislikes", 0);
            song.put("votes", 0);

            mAdapter.addItem(song);
        }

    }

    public void playSong(String songName)
    {
        if (mp == null)
        {
            mp = MediaPlayer.create(DJActivity.this, Uri.parse(path + songName));

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

    private class MyCustomAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final HashMap<String, Object> item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public int getCount() {
            return mData.size();
        }


        public HashMap<String, Object> getItem(int position) {
            return mData.get(position);
        }


        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.line, null);
                holder = new ViewHolder();
                holder.songNameText = (TextView)convertView.findViewById(R.id.songNameText);
                holder.likesText = (TextView) convertView.findViewById(R.id.likesText);
                holder.dislikesText = (TextView) convertView.findViewById(R.id.dislikesText);
                holder.votesText = (TextView) convertView.findViewById(R.id.votesText);
                holder.playButtonImage = (ImageView) convertView.findViewById(R.id.playButtonImage);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.songNameText.setText(((String)mData.get(position).get("songName")) + "      ");
            holder.likesText.setText("" + ((int)mData.get(position).get("likes")) + " likes      ");
            holder.dislikesText.setText("" + ((int)mData.get(position).get("dislikes")) + " dislikes      ");
            holder.votesText.setText("" + ((int)mData.get(position).get("votes")) + " votes      ");
            holder.playButtonImage.setImageResource(R.drawable.baseline_play_circle_outline_24);

            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView songNameText;
        public TextView likesText;
        public TextView dislikesText;
        public TextView votesText;
        public ImageView playButtonImage;
    }

}
