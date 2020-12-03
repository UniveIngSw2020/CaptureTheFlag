package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class CreateUserActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        TextView tv = findViewById(R.id.createusertv);
        tv.setText("SONO NELLA USER CREATE ACTIVITY");

        // this create the user's file if not exists in the local file manager ¯\_(ツ)_/¯
        final StoredDataManager sdm = new StoredDataManager(CreateUserActivity.this.getFilesDir());

        // finds the input field and button
        final EditText edit_name = findViewById(R.id.inputname);
        Button bt = findViewById(R.id.inputnamebutton);

        // test for the stored data
        Toast.makeText(CreateUserActivity.this,
                sdm.readData().toString(),
                Toast.LENGTH_LONG).show();

        // when the button is clicked create the user and set the fields, then write in the local file
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LocalUser user = new LocalUser();
                user.setId(IdGenerator.generatePlayerId());
                user.setScore(0, 0, 0);
                user.setName(edit_name.getText().toString());
                sdm.writeFile(user);
                finish();
            }
        });

    }
}