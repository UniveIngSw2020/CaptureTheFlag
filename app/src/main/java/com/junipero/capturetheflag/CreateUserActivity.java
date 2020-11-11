package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CreateUserActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        TextView tv = findViewById(R.id.createusertv);
        tv.setText("SONO NELLA USER CREATE ACTIVITY");

        // this create the user's file if not exists in the local file manager ¯\_(ツ)_/¯
        StoredDataManager sdm = new StoredDataManager(CreateUserActivity.this.getFilesDir());
        /*
        Toast.makeText(CreateUserActivity.this,
                sdm.readFile().toString(),
                Toast.LENGTH_LONG).show();

         */

        /* TEST PER GSON data manager
            User user = new User();
            user.setId(1);
            user.setName("Luca");
            user.setWins(1);
            user.setLosts(1);
            user.setTies(1);
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            System.out.println(userJson);

         */




    }
}