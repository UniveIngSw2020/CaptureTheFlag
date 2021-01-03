package com.junipero.capturetheflag;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class GameDB {

    private DatabaseReference dbRef = null;
    //private final String MyName = "Nasi";

    public GameDB() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                /*
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);

                 */

                /*
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + map);

                 */


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public DatabaseReference getDbRef() {
        return dbRef;
    }

    /*
    public void setValue(String value){
        dbRef.setValue(value);
    }

     */

    // actually can't set the child in the db ¯\_(ツ)_/¯
    /*
    public void setChild(String child){
        dbRef.child(child);
    }

     */


}
