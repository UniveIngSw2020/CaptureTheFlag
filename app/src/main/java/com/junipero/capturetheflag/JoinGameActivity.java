package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class JoinGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        TextView test = findViewById(R.id.joingametv);
        test.setText("SONG NELLA JOIN GAMEEEE");

        final EditText edit_game = findViewById(R.id.inputidgame);
        final TextView wait_game = findViewById(R.id.waitstart);
        Button joinButton = findViewById(R.id.joinbutton);
        final DatabaseReference lobby = new GameDB().getDbRef();
        final StoredDataManager me = new StoredDataManager(JoinGameActivity.this.getFilesDir());

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lobby.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //checking if the lobby exists
                        if (snapshot.hasChild(edit_game.getText().toString())){
                            Toast.makeText(JoinGameActivity.this, "Room TROVATA", Toast.LENGTH_LONG)
                                    .show();

                            lobby.child(edit_game.getText().toString()).child("Players")
                                .child(me.readID()).setValue(me.readName());

                            //write code to perform in db
                            // if > 10 cannot enter

                            wait_game.setText("waiting to start the game..");
                        }else{
                            Toast.makeText(JoinGameActivity.this, "Room NON TROVATA", Toast.LENGTH_LONG)
                                    .show();

                            //cannot press the join button
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                    Toast.makeText(JoinGameActivity.this, "JOINNATO", Toast.LENGTH_LONG).show();
            }
        });
    }
}