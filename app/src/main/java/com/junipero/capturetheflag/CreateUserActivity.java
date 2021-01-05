package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateUserActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        // declaring and setting a text of this Activity
        TextView tv = findViewById(R.id.createusertv);
        tv.setText("Insert your nickname");

        // this create the user's file if not exists in the local file manager ¯\_(ツ)_/¯
        final StoredDataManager sdm = new StoredDataManager(CreateUserActivity.this.getFilesDir());

        // finds the input field and button
        final EditText edit_name = findViewById(R.id.inputname);
        Button bt = findViewById(R.id.inputnamebutton);

        // when the button is clicked create the user and set the fields,
        // then write into the local file
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // check if the user's entered a valid username
                // then create the new user with new fresh stats
                if(!edit_name.getText().toString().equals("")){
                    LocalUser user = new LocalUser();
                    user.setId(IdGenerator.generatePlayerId());
                    user.setScore(0, 0, 0);
                    user.setName(edit_name.getText().toString());
                    sdm.writeFile(user);
                    finish();
                }else{
                    // error message displayed as a Toast
                    Toast.makeText(CreateUserActivity.this,
                            "Please insert a new nickname", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() { /* can't press back */ }
}