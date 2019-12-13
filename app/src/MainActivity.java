package com.example.djrai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
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
    static int currentSkipVotes;
    static int currentVotesForNewDJ;
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
        checkPermission();
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                // request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        } else {
            // Permission has already been granted
            start_button.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    start_button.setEnabled(true);
                } else {
                    // permission denied
                    start_button.setEnabled(false);
                }
                return;
            }
        }
    }

    public void initializeDatabase() {
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
    }


    public static void updateSkipVotes() {
        skipRef.setValue(currentSkipVotes + 1);
    }

    public static void updateNewDJVotes() {
        chooseDJRef.setValue(currentVotesForNewDJ + 1);
    }

    public static void createSkipReader() {
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

    public static void createDJVotesReader() {
        chooseDJRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                currentVotesForNewDJ = value;
                Log.d(TAG, "Number of people who want a new DJ is " + currentVotesForNewDJ);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read dj votes value.", error.toException());
            }
        });
    }

    public static void createSongsListReader() {
        songsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
        roomRef = rootRef.child(roomID);
        attendeesRef = roomRef.child("peopleList");
        attendeesCountRef = roomRef.child("attendeesCount");
        totalJoinsEverRef = roomRef.child("totalJoinsEver");
        skipRef = roomRef.child("skipVotes");
        songsRef = roomRef.child("songList");
        chooseDJRef = roomRef.child("newDJVotes");
        createSkipReader();
        goToDJActivity();
    }

    public void joinRoomAsListener() {
        roomRef = rootRef.child(roomID);
        attendeesRef = roomRef.child("peopleList");
        attendeesCountRef = roomRef.child("attendeesCount");
        totalJoinsEverRef = roomRef.child("totalJoinsEver");
        skipRef = roomRef.child("skipVotes");
        songsRef = roomRef.child("songList");
        chooseDJRef = roomRef.child("newDJVotes");
        createSkipReader();
        final Person person = new Person("regular", 0);
        final Map<String, Person> peopleList = new HashMap<String, Person>();
        final Map<String, Object> childUpdates = new HashMap<>();

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalJoinsEver = dataSnapshot.child("totalJoinsEver").getValue(Integer.class);
                currentJoins = dataSnapshot.child("attendeesCount").getValue(Integer.class);
                ArrayList<Person> currentPeopleInRoom = (ArrayList<Person>) dataSnapshot.child("peopleList").getValue();

                totalJoinsEver = totalJoinsEver + 1;
                currentJoins = currentJoins + 1;
                attendeesCountRef.setValue(currentJoins);
                totalJoinsEverRef.setValue(totalJoinsEver);
                userID = "" + totalJoinsEver;

                for (int i = 0; i < currentPeopleInRoom.size(); i++) {
                    peopleList.put("" + i, currentPeopleInRoom.get(i));
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
