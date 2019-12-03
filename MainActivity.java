package com.example.djrai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button start_button;
    Button instructions_button;
    Boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_button = (Button)findViewById(R.id.joinserver_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoom();
            }
        });
        instructions_button = (Button)findViewById(R.id.instructions_button);
        instructions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInstructions();
            }
        });
    }

    public void startRoom()
    {
        //if first to join server, start DJActivity
        Intent DJIntent = new Intent(this, DJActivity.class);
        startActivity(DJIntent);
        //else, start Listener Activity
        if (!first) {
            Intent ListenerIntent = new Intent(this, ListenerActivity.class);
            startActivity(ListenerIntent);
        }
    }

    public void startInstructions()
    {
        //to be created later.....
        Intent InstructionsIntent = new Intent(this, InstructionsActivity.class);
        startActivity(InstructionsIntent);
    }
}
