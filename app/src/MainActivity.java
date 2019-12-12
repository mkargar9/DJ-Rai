package com.example.djrai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button start_button;
    Button instructions_button;
    EditText roomIDEditText;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    DatabaseReference roomRef;
    DatabaseReference songsRef;
    DatabaseReference attendeesRef;
    DatabaseReference chooseDJRef;
    DatabaseReference skipRef;
    String roomID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_button = (Button)findViewById(R.id.joinroom_button);
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
        roomIDEditText = (EditText) findViewById(R.id.enterRoomID);

        initializeDatabase();
        //createSkipReader();
    }

    public void initializeDatabase() {
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        /*
        roomRef = rootRef.child("room1");
        songsRef = roomRef.child("songs");
        attendeesRef = roomRef.child("attendees");
        chooseDJRef = roomRef.child("djVotes");
        skipRef = roomRef.child("skipVotes");

         */
    }

    /*
    public void updateSkipVotes() {
        skipRef.setValue(currentSkipVotes + 1);
    }
    */
/*
    public void createSkipReader() {
        skipRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                currentSkipVotes = value;
                Log.d(TAG, "Skip votes value is: " + currentSkipVotes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read skip votes value.", error.toException());
            }
        });
    }

 */

    public void startRoom()
    {
        roomID = roomIDEditText.getText().toString().trim();
        if (roomID.length() == 0) {
            // when roomID is blank, make toast popup here and ask user to enter room name again
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Room name is blank. Please try again.",
                    Toast.LENGTH_SHORT);

            toast.show();
        }
        else {
            final Intent ListenerIntent = new Intent(this, ListenerActivity.class);
            final Intent DJIntent = new Intent(this, DJActivity.class);

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(roomID)) {
                        // if room already exists, join as listener
                        startActivity(ListenerIntent);
                    }
                    else {
                        //if room does not exist, person becomes dj and room is created
                       
                        startActivity(DJIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value when checking if room exists.", error.toException());
                }
            });
        }
    }

    public void startInstructions()
    {
        //to be created later.....
        Intent InstructionsIntent = new Intent(this, InstructionsActivity.class);
        startActivity(InstructionsIntent);
    }

}
