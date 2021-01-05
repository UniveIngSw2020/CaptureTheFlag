package com.junipero.capturetheflag;

import android.annotation.SuppressLint;

public class LocalUser {

    private String id;
    private String name;
    private int wins, losts, ties;

    // default constructor
    public LocalUser(){
        this.id = "0";
        this.name = "None";
        this.wins = 0;
        this.losts = 0;
        this.ties = 0;
    }

    // ---------------- SETTER -------------------------
    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setWins(int wins) { this.wins = wins; }

    public void setLosts(int losts) { this.losts = losts; }

    public void setTies(int ties) { this.ties = ties; }

    public void setScore(int wins, int losts, int ties){
        this.wins = wins;
        this.losts = losts;
        this.ties = ties;
    }

    // -------- GETTER ------------------
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
