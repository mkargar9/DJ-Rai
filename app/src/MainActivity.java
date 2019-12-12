package com.example.djrai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button start_button;
    Button instructions_button;
    Button test_button;
    TextView debugPrintSkip;
    Boolean first = true;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    DatabaseReference songsRef;
    DatabaseReference attendeesRef;
    DatabaseReference chooseDJRef;
    DatabaseReference skipRef;
    int currentSkipVotes;


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
        test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFirebaseSkipUpdates();
            }
        });

        debugPrintSkip = (TextView) findViewById(R.id.debugPrintSkip);
        initializeDatabase();
        createSkipReader();
    }

    public void initializeDatabase() {
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        songsRef = rootRef.child("Songs");
        attendeesRef = rootRef.child("attendees");
        chooseDJRef = rootRef.child("choose dj");
        skipRef = rootRef.child("skip");
    }

    public void updateSkipVotes() {
        skipRef.setValue(currentSkipVotes + 1);
    }

    public void createSkipReader() {
        skipRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                currentSkipVotes = value;
                debugPrintSkip.setText("Current Skip Votes from firebase: " + currentSkipVotes);
                Log.d(TAG, "Skip votes value is: " + currentSkipVotes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read skip votes value.", error.toException());
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

    public void testFirebaseSkipUpdates()
    {
        updateSkipVotes();
    }
}