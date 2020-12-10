package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CreateGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        TextView test = findViewById(R.id.creategametv);
        test.setText("SONO NELLA CREATE GAME");

        final Button startbutton = findViewById(R.id.startbutton);

        TextView gameId = findViewById(R.id.idgame);
        final TextView players_in_room = findViewById(R.id.playersnumber);
        /* uncomment lately to use random rooms
        String gameCode = IdGenerator.generateMatchId();
        gameId.setText(gameCode);
        */


        /* DEBUG room */
        String gameCode = "l95a";
        gameId.setText(gameCode);

        StoredDataManager me = new StoredDataManager(CreateGameActivity.this.getFilesDir());

        // ref to lobby
        DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        // set the current state of the game
        lobby.child("State").setValue("Waiting for start");
        // adding myself to the lobby
        DatabaseReference players = lobby.child("Players");
        players.child(me.readID())
            .setValue(me.readName());

        // get actual players in room
        players.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players_in_room.setText(snapshot.getChildrenCount() + "");
                if(snapshot.getChildrenCount() < 4 ){
                    startbutton.setEnabled(false);
                }else{
                    startbutton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGameActivity.this, GameActivity.class));
            }
        });



    }
}