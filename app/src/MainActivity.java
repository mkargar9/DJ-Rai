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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static FirebaseDatabase database;
    static DatabaseReference rootRef;
    static DatabaseReference roomRef;
    static DatabaseReference songsRef;
    static DatabaseReference attendeesRef;
    static DatabaseReference chooseDJRef;
    static DatabaseReference skipRef;
    static DatabaseReference attendeesCountRef;
    static DatabaseReference totalJoinsEverRef;
    static String roomID;
    static String userID;
    static int totalJoinsEver;
    static int currentJoins;
    Button start_button;
    Button instructions_button;
    EditText roomIDEditText;


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
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(roomID)) {
                        // if room already exists, join as listener
                        joinRoomAsListener();
                    }
                    else {
                        //if room does not exist, person becomes dj and room is created
                        joinRoomAsDJ();
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

    public void goToDJActivity() {
        Intent DJIntent = new Intent(this, DJActivity.class);
        startActivity(DJIntent);
    }

    public void goToListenerActivity() {
        Intent ListenerIntent = new Intent(this, ListenerActivity.class);
        startActivity(ListenerIntent);
    }

    public void joinRoomAsDJ() {
        Person person = new Person("DJ", 0);
        Song song = new Song(0, 0, 0);
        Map<String, Person> peopleList = new HashMap<String, Person>();
        peopleList.put("1", person);
        Map<String, Song> songList = new HashMap<String, Song>();
        songList.put("Despacito by Luis Fonsi", song);
        Room room = new Room(0, 0, 1, 1, peopleList, songList);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + roomID + "/", room.toMap());
        rootRef.updateChildren(childUpdates);
        goToDJActivity();
    }

    public void joinRoomAsListener() {
        roomRef = rootRef.child(roomID);
        attendeesRef = roomRef.child("peopleList");
        attendeesCountRef = roomRef.child("attendeesCount");
        totalJoinsEverRef = roomRef.child("totalJoinsEver");
        final Person person = new Person("regular", 0);
        final Map<String, Person> peopleList = new HashMap<String, Person>();
        final Map<String, Object> childUpdates = new HashMap<>();

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalJoinsEver = dataSnapshot.child("totalJoinsEver").getValue(Integer.class);
                currentJoins = dataSnapshot.child("attendeesCount").getValue(Integer.class);
                Map<String, Person> currentPeopleInRoom = (Map<String, Person>) dataSnapshot.child("peopleList").getValue();

                totalJoinsEver = totalJoinsEver + 1;
                currentJoins = currentJoins + 1;
                attendeesCountRef.setValue(currentJoins);
                totalJoinsEverRef.setValue(totalJoinsEver);
                userID = "" + totalJoinsEver;

                for (Map.Entry<String, Person> entry : currentPeopleInRoom.entrySet()) {
                    peopleList.put(entry.getKey(), entry.getValue());
                }

                peopleList.put(userID, person);

                childUpdates.put("/peopleList/", peopleList);
                roomRef.updateChildren(childUpdates);


                goToListenerActivity();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value when joining", error.toException());
            }
        });
    }

    public static class Person {
        public String role;
        public int votesNextDJ;

        public Person() {
            // Default constructor
        }

        public Person(String role, int votesNextDJ) {
            this.role = role;
            this.votesNextDJ = votesNextDJ;
        }
    }

    public static class Song {
        public int dislikes;
        public int likes;
        public int votesPlayNext;

        public Song() {
            // Default constructor
        }

        public Song(int dislikes, int likes, int votesPlayNext) {
            this.dislikes = dislikes;
            this.likes = likes;
            this.votesPlayNext = votesPlayNext;
        }
    }

    public static class Room {
        public int newDJVotes;
        public int skipVotes;
        public int totalJoinsEver;
        public int attendeesCount;
        public Map<String, Person> peopleList;
        public Map<String, Song> songList;

        public Room() {
            // Default constructor
        }

        public Room(int newDJVotes, int skipVotes, int totalJoinsEver, int attendeesCount,
                    Map<String, Person> peopleList, Map<String, Song> songList) {
            this.newDJVotes = newDJVotes;
            this.skipVotes = skipVotes;
            this.totalJoinsEver = totalJoinsEver;
            this.attendeesCount = attendeesCount;
            this.peopleList = peopleList;
            this.songList = songList;
        }

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("newDJVotes", newDJVotes);
            result.put("skipVotes", skipVotes);
            result.put("totalJoinsEver", totalJoinsEver);
            result.put("attendeesCount", attendeesCount);
            result.put("peopleList", peopleList);
            result.put("songList", songList);

            return result;
        }
    }
}
