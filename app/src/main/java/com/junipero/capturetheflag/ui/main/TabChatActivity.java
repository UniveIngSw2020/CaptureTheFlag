package com.junipero.capturetheflag.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.junipero.capturetheflag.R;

public class TabChatActivity extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = null;
        root = inflater.inflate(R.layout.fragment_chat, container, false);

        return root;
    }

    // put your code in onViewCreated, it is called after onCreateView
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // put your code here ¯\_(ツ)_/¯

    }
}
