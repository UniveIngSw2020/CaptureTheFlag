package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class JoinGameActivity extends AppCompatActivity {

    boolean isAdded = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        TextView test = findViewById(R.id.joingametv);
        test.setText("SONO NELLA JOIN GAMEEEE");

        final EditText edit_game = findViewById(R.id.inputidgame);
        final TextView wait_start = findViewById(R.id.waitstart);
        final Button joinButton = findViewById(R.id.joinbutton);
        final DatabaseReference lobby = new GameDB().getDbRef();
        final StoredDataManager me = new StoredDataManager(JoinGameActivity.this.getFilesDir());

        // join button event actions
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lobby.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //checking if the lobby exists
                        if (snapshot.hasChild(edit_game.getText().toString())){
                            Toast.makeText(JoinGameActivity.this, "Room TROVATA", Toast.LENGTH_SHORT)
                                    .show();

                            lobby.child(edit_game.getText().toString()).child("Players")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getChildrenCount() < 10){

                                        // inserting my info in players table in the db
                                        lobby.child(edit_game.getText().toString()).child("Players")
                                                .child(me.readID()).setValue(me.readName());

                                        isAdded = true;
                                        //players.child(me.readID()).setValue(me.readName());
                                        //write code to perform in db
                                        // if > 10 cannot enter
                                        Toast.makeText(JoinGameActivity.this,  Long.valueOf(snapshot.getChildrenCount()).toString() , Toast.LENGTH_SHORT).show();
                                        wait_start.setText("waiting to start the game...");

                                        // then check if the status changed into "Timer"
                                        lobby.child(edit_game.getText().toString())
                                                .child("State").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // here's the checker, then start the Timer activity
                                                if (snapshot.getValue().toString().equals("Timer")){
                                                    startActivity(new Intent(JoinGameActivity.this, GameActivity.class));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }else{
                                        Toast.makeText(JoinGameActivity.this, "Capienza massima raggiunta", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    // need to add if for the state of the game
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else{
                            Toast.makeText(JoinGameActivity.this, "Room NON TROVATA", Toast.LENGTH_SHORT)
                                    .show();

                            //cannot press the join button
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}