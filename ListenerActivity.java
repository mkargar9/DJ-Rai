package com.example.djrai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListenerActivity extends MainActivity {

    ListView lv;
    List<String> list;
    Button skip;
    Button newDJ;
    boolean newDJRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        skip = (Button)findViewById(R.id.skip_button);
        newDJ = (Button)findViewById(R.id.newdj_button);

        list = new ArrayList<String>();
        list.add("\"Despacito\" by Luis Fonsi");
        list.add("\"Video Games\" by Lana Del Rey");
        list.add("\"All I Want for Christmas\" by Mariah Carey");

        lv = (ListView)findViewById(R.id.song_listview2);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListenerActivity.super.updateSkipVotes();
            }
        });

        newDJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newDJRequested)
                {
                    ListenerActivity.super.updatenewDJVotes();
                    newDJRequested = true;
                    newDJ.setEnabled(false);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You can only request a new DJ once",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
            }
        });
    }
}