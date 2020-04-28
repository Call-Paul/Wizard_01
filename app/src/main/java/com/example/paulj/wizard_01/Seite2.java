package com.example.paulj.wizard_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Seite2 extends AppCompatActivity {

    ListView listview;
    Button button;

    List<String> roomsList;
    String playername="";
    String roomName="";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seite2);


        //get Player name and assign his room name to the player name
        database=FirebaseDatabase.getInstance();
        SharedPreferences preferences=getSharedPreferences("PREFS",0);
        playername=preferences.getString("playerName","");
        roomName=playername;
        listview=findViewById(R.id.list);
        button=findViewById(R.id.button2);

        //all existing avalible rooms
        roomsList=new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setText("Create new Room");
                button.setEnabled(false);
                roomName=playername;
                roomRef=database.getReference("rooms/"+roomName+"/player1");
                addRoomEventListener();
                roomRef.setValue(playername);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                roomName=roomsList.get(i);
                roomRef=database.getReference("rooms/"+roomName+"/player2");
                addRoomEventListener();
                roomRef.setValue(playername);

            }
        });




    }

    private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                button.setText("Create Room");
                button.setEnabled(true);
                Intent intent=new Intent(getApplicationContext(),Seite3.class);
                intent.putExtra("roomName",roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                button.setText("Create new Room");
                button.setEnabled(true);
                Toast.makeText(Seite2.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsVentListner(){
        roomsRef=database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomsList.clear();
                Iterable<DataSnapshot>rooms=dataSnapshot.getChildren();
                for(DataSnapshot snapshot:rooms){
                    roomsList.add(snapshot.getKey());

                    ArrayAdapter<String> adapter=new ArrayAdapter<>(Seite2.this,android.R.layout.simple_list_item_1,roomsList);
                    listview.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
