package com.junipero.capturetheflag.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.junipero.capturetheflag.GameDB;
import com.junipero.capturetheflag.R;
import java.util.HashMap;
import java.util.Map;

public class TabChatActivity extends Fragment {

    // declaration of some parameters in this view
    private Intent i;
    private String gameCode, role, team, id, name, temp_key;
    private TextView chat_msg;
    private ScrollView scrollChat;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = null;
        root = inflater.inflate(R.layout.fragment_chat, container, false);

        return root;
    }

    // put your code in onViewCreated, it is called after onCreateView
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // code for CHAT section
        // get all datas from previous activity
        i = this.getActivity().getIntent();
        gameCode = i.getStringExtra("gameCode");
        role = i.getStringExtra("role");
        team = i.getStringExtra("team");
        id = i.getStringExtra("id");
        name = i.getStringExtra("name");

        // initialize the views for this fragment
        final EditText input_msg = view.findViewById(R.id.inputMsg);
        Button send_btn = view.findViewById(R.id.sendBtn);
        chat_msg = view.findViewById(R.id.msgView);
        scrollChat = view.findViewById(R.id.scrollChat);

        final DatabaseReference room = new GameDB().getDbRef().child(gameCode + "/" + team + "/Chat");

        // map the message and send it to the db
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_msg.getText().toString().equals("")){
                    Map<String,Object> map = new HashMap<>();
                    temp_key = room.push().getKey();
                    room.updateChildren(map);

                    DatabaseReference msg_root = room.child(temp_key);
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("Name", name);
                    map2.put("Message", input_msg.getText().toString());

                    msg_root.updateChildren(map2);
                    input_msg.setText("");
                }
            }
        });

        chat_msg.setText(Html.fromHtml("<b> Team " + team + "</b>: Welcome here!"));

        // update the conversation history
        room.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                append_chat_conversation(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                append_chat_conversation(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    // this method allow users to see last messages sent in bottom of the screen
    private void append_chat_conversation (DataSnapshot snapshot){
        String name = "", msg = "";
        if(snapshot.child("Name").getValue() != null &&
                snapshot.child("Message").getValue() != null) {
            name = snapshot.child("Name").getValue().toString();
            msg = snapshot.child("Message").getValue().toString();
            chat_msg.append(Html.fromHtml("<br/><b>" + name + "</b>: " + msg));

            scrollChat.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollChat.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },500);
        }

    }

}
