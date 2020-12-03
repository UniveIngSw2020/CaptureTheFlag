package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class CreateGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        TextView test = findViewById(R.id.creategametv);
        test.setText("SONO NELLA CREATE GAME");

        TextView gameId = findViewById(R.id.idgame);
        String gameCode = IdGenerator.generateMatchId();
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

        // if >4 can start the game



    }
}