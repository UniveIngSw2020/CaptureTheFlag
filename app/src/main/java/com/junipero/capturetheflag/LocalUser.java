package com.junipero.capturetheflag;

import android.annotation.SuppressLint;

import java.io.Serializable;

public class LocalUser implements Serializable {

    private String id;
    private String name;
    private int wins, losts, ties;

    // ---------------- GETTER -------------------------

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setWins(int wins) { this.wins = wins; }

    public void setLosts(int losts) { this.losts = losts; }

    public void setTies(int ties) { this.ties = ties; }

    // -------- SETTER ------------------
    public String getId() { return id; }

    public String getName() { return name; }

    public int getWins() { return wins; }

    public int getLosts() { return losts; }

    public int getTies() { return ties; }




    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("User [id=%s, name=%s, wins=%d, losts=%d, ties=%d]"
                , id, name, wins, losts, ties);
    }

}
