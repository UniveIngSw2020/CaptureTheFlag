package com.junipero.capturetheflag.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.junipero.capturetheflag.R;

public class TabChatActivity extends Fragment {

    Intent i;
    String gameCode;
    String role;
    String team;

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

        // put your code here ¯\_(ツ)_/¯

    }
}
