package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
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

    private String gameCode;
    private DatabaseReference lobby;
    private boolean isChangingActivity = false;
    private boolean isGoingToBackground = true;


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
        test.setText("CREATE A LOBBY");

        final Button startbutton = findViewById(R.id.startbutton);
        final ArrayList<Pair<String, String>> players_list = new ArrayList<>();

        TextView gameId = findViewById(R.id.idgame);
        final TextView players_in_room = findViewById(R.id.playersnumber);
        // uncomment lately to use random rooms
        gameCode = IdGenerator.generateMatchId();
        gameId.setText(Html.fromHtml("The code for your lobby is: " + "<b>"+gameCode+"</b>"));

        // if not connected to internet return in Main screen
        if(!isNetworkConnected()){
            gameId.setText("Internet connection is not enabled");
            startbutton.setEnabled(false);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }


        /* DEBUG room
        final String gameCode = "abcd";
        gameId.setText(gameCode);

         */

        // get my data in a StoredDataManager object
        StoredDataManager me = new StoredDataManager(CreateGameActivity.this.getFilesDir());

        // ref to lobby
        lobby = new GameDB().getDbRef().child(gameCode);
        // set the current state of the game
        lobby.child("State").setValue("Waiting for start");
        // adding myself to the lobby
        final DatabaseReference players = lobby.child("Players");
        players.child(me.readID()).setValue(me.readName());

        /* additional debug players added to start testing of room */
        players.child("736848276348236687").setValue("nasi2");
        players.child("736848276348234211287").setValue("nasi3");
        players.child("73684827634822346687").setValue("nasi4");
        /*
        players.child("7368482314142346687").setValue("nasi5");

        players.child("7368484554622346687").setValue("nasi6");
        players.child("7387388374896687").setValue("nasi7");
         */


        // get actual players in room  and enable/disable start button
        players.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players_in_room.setText("Players: " + snapshot.getChildrenCount() + "/10");
                if(snapshot.getChildrenCount() < 4 ){
                    startbutton.setEnabled(false);
                    startbutton.setAlpha(0.8f);
                }else{
                    players_list.clear();
                    // get all players as <String, String> (key: ID, value: Name) then adding to an ArrayList
                    for(DataSnapshot child : snapshot.getChildren()){
                        players_list.add(new Pair<String, String>(child.getKey(),
                                child.getValue().toString()));
                    }
                    startbutton.setEnabled(true);
                    startbutton.setAlpha(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // on click action manage players data to put in the db
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // declaration of the final arrayList with team and role for each player
                ArrayList<Quadruple<String, String, String, String>> final_players_list =
                        new ArrayList<>(players_list.size());
                Collections.shuffle(players_list);


                // shunt algorithm
                for(int i = 0; i < players_list.size(); i++){
                    String team = (i % 2 == 0) ? "Red" : "Blue";
                    String role = (i < 2) ? "Keeper" : "Stealer";
                    /*
                        Quadruple item contains:
                            - first: Id of player
                            - second: Name of player
                            - third: role in the game in his team
                            - fourth: his team color
                    */
                    final_players_list.add(new Quadruple<>(players_list.get(i).first,
                            players_list.get(i).second,
                            role,
                            team));
                }

                // inssert into db and delete Players child
                // delete Players from the actual lobby
                lobby.child("Players").removeValue();

                // crate teams in db then add players in their team
                for (Quadruple player : final_players_list){
                    // gameCode -> team -> role -> Id -> name
                    // adding the nme of player into his opportune location in db
                    lobby.child(player.fourth.toString())
                            .child(player.third.toString())
                            .child(player.first.toString())
                            .setValue(player.second.toString());

                }
                // set State of current game in "Timer" after shunting
                lobby.child("State").setValue("Timer");

                // set the number of players
                lobby.child("Number of players").setValue(players_list.size());

                // -> timer Activity
                Intent i = new Intent(CreateGameActivity.this, TimerActivity.class);
                i.putExtra("gameCode", gameCode);
                lobby.child("State").setValue("in Game");
                isChangingActivity = true;
                startActivity(i);
                finish();


            }
        });
    }

    // internet connection checker
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isGoingToBackground = false;
        //lobby.removeValue();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // if the app is in background stop the music
        if(isGoingToBackground){
            stopService(new Intent(CreateGameActivity.this, BackgroundSoundService.class));
        }

        if(!isChangingActivity){
            lobby = new GameDB().getDbRef().child(gameCode);
            // delete the game
            lobby.removeValue();
        }

        finish();
    }
}
