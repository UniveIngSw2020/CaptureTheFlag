package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class CreateGameActivity extends AppCompatActivity {

    // declaration of a Quadruple class
    private class Quadruple<A, B, C, D>{
        private final A first;
        private final B second;
        private final C third;
        private final D fourth;

        public Quadruple(A first, B second, C third, D fourth){
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        TextView test = findViewById(R.id.creategametv);
        test.setText("SONO NELLA CREATE GAME");

        final Button startbutton = findViewById(R.id.startbutton);
        final ArrayList<Pair<String, String>> players_list = new ArrayList<>();

        TextView gameId = findViewById(R.id.idgame);
        final TextView players_in_room = findViewById(R.id.playersnumber);
        // uncomment lately to use random rooms

        String gameCode = IdGenerator.generateMatchId();
        gameId.setText(gameCode);





        /* DEBUG room */
        /*
        String gameCode = "l95a";
        gameId.setText(gameCode);
        */

        // get my data in a StoredDataManager object
        StoredDataManager me = new StoredDataManager(CreateGameActivity.this.getFilesDir());

        // ref to lobby
        final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        // set the current state of the game
        lobby.child("State").setValue("Waiting for start");
        // adding myself to the lobby
        DatabaseReference players = lobby.child("Players");
        players.child(me.readID())
            .setValue(me.readName());

        // get actual players in room  and enable/disable start button
        players.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players_in_room.setText(snapshot.getChildrenCount() + "");
                if(snapshot.getChildrenCount() < 4 ){
                    startbutton.setEnabled(false);
                }else{
                    // get all players as <String, String> (key, value) then adding to an ArrayList
                    for(DataSnapshot child: snapshot.getChildren()){
                        players_list.add(new Pair<String, String>(child.getKey(), child.getValue().toString()));
                    }
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
                // declaration of the final arrayList with team and role for each player
                ArrayList<Quadruple<String, String, String, String>> final_players_list = new ArrayList<>(players_list.size());
                Collections.shuffle(players_list);

                // shunt algorithm
                for(int i = 0; i < players_list.size(); i++){
                    String team = (i % 2 == 0) ? "Red" : "Blue";
                    String role = (i < 2) ? "Keeper" : "Stealer";
                    final_players_list.add(new Quadruple<>(players_list.get(i).first, players_list.get(i).second, role, team));
                }

                /* toast debug
                for (Quadruple q : final_players_list){
                    Toast.makeText(CreateGameActivity.this, q.first.toString()+"\n"
                    +q.second.toString()+"\n"
                    +q.third.toString()+"\n"
                            + q.fourth.toString(), Toast.LENGTH_SHORT).show();
                }

                 */

                // inssert into db and delete Players child
                // delete Players in db

                //startActivity(new Intent(CreateGameActivity.this, GameActivity.class));
            }
        });



    }
}