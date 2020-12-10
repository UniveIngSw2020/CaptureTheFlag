package com.junipero.capturetheflag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StoredDataManager {

    private File root;
    private File data;
    private final String folder = "userdata";
    private final String dataFileName = "data.json";

    public StoredDataManager(File root) {
        this.root = new File(root, folder);
        createFolderIfNotExists();
    }

    private void createFolderIfNotExists() {
        if (!root.exists()) {
            root.mkdir();
            data = createFile();
            writeFile(new LocalUser());
            //writeFile(createFileIfNotExists());
        }else{
            data = createFile();
        }
    }

    private File createFile() {
        return new File(root, dataFileName);
    }

    // writing in json file stored inside the phone
    public void writeFile(LocalUser user) {

        // write new data in the local file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userToJson = gson.toJson(user);

        try {
            FileWriter fw = new FileWriter(data);
            // can be used append() or write()
            //fw.append("Ciao ho scritto forse la mia prima riga in un file dentro Android");

            //fw.write("ID: \nName: \nWins: \nLosts: \nTies: \n" );
            fw.write(userToJson);
            fw.flush();
            fw.close();
            //output.setText(readFile());
            //Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder generateStringBuilderData(){
        File file = new File(root, dataFileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                text.append(line).append('\n');
            }
            br.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        return text;
    }

    public LocalUser getUser(){
        StringBuilder sb = this.generateStringBuilderData();
        Gson gson = new GsonBuilder().create();
        LocalUser user = gson.fromJson(sb.toString(), LocalUser.class);

        return user;
    }

    public String readData(){
        /*
        StringBuilder sb = this.generateStringBuilderData();
        Gson gson = new GsonBuilder().create();
        LocalUser user = gson.fromJson(sb.toString(), LocalUser.class);
         */

        LocalUser user = getUser();

        return  "ID: " + user.getId() + "\n" +
                "NAME: " + user.getName() + "\n" +
                "WINS: " + user.getWins() + "\n" +
                "LOSTS: " + user.getLosts() + "\n" +
                "TIES: " + user.getTies() + "\n";

    }

    public String readName(){
        return getUser().getName();
    }
    public String readID(){
        return getUser().getId();
    }

    public void changeUsername (String username){
        LocalUser me = getUser();
        me.setName(username);
        writeFile(me);
    }

    public void increaseWins(){
        LocalUser me = getUser();
        int counter = me.getWins() + 1;
        me.setWins(counter);
        writeFile(me);
    }

    public void increaseLosts(){
        LocalUser me = getUser();
        int counter = me.getLosts() + 1;
        me.setLosts(counter);
        writeFile(me);
    }

    public void increaseTies(){
        LocalUser me = getUser();
        int counter = me.getTies() + 1;
        me.setTies(counter);
        writeFile(me);
    }

}
