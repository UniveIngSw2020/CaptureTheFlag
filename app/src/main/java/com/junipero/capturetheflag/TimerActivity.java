package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.TestLooperManager;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.widget.Toast.LENGTH_SHORT;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        final ConstraintLayout layout = findViewById(R.id.timerLayout);
        Intent i = getIntent();
        String gameCode = i.getStringExtra("gameCode");

        TextView gameCodeViewer = findViewById(R.id.GameID);
        gameCodeViewer.setText(gameCode);

        final TextView roleViewer = findViewById(R.id.roleView);
        final TextView timerViewer = findViewById(R.id.timeView);

        final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        final StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());

        gameCodeViewer.setTextColor(Color.WHITE);
        roleViewer.setTextColor(Color.WHITE);
        timerViewer.setTextColor(Color.WHITE);



        // get your data from db
        lobby.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.child("Red/Keeper").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team RED, and your role is : Keeper
                        roleViewer.setText("Keeper");
                        layout.setBackgroundColor(Color.RED);
                    }
                }
                for (DataSnapshot ds : snapshot.child("Red/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team RED, and your role is : Stealer
                        roleViewer.setText("Stealer");
                        layout.setBackgroundColor(Color.RED);
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Keeper").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Keeper
                        roleViewer.setText("Keeper");
                        layout.setBackgroundColor(Color.BLUE);
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Stealer
                        roleViewer.setText("Stealer");
                        layout.setBackgroundColor(Color.BLUE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Countdown 1 minute
        new CountDownTimer(60000, 1000){
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                timerViewer.setText("time remaining: \n" + (millisUntilFinished / 1000) + " seconds");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                //timerViewer.setText("Let's start!");
            }
        }.start();





    }

    // delete all data of current game stored in db
    @Override
    public void onBackPressed() {
        // get the gameCode from the Intent
        Intent i = getIntent();
        String gameCode = i.getStringExtra("gameCode");
        DatabaseReference db = new GameDB().getDbRef().child(gameCode);
        // then remove data
        db.removeValue();
    }

}
