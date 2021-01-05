package com.junipero.capturetheflag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StoredDataManager {

    private File root;  // root directory of your Android device
    private File data;  // file where to update data
    private final String folder = "userdata";   // folder name
    private final String dataFileName = "data.json";    // file name

    public StoredDataManager(File root) {
        this.root = new File(root, folder);
        createFolderIfNotExists();
    }

    private void createFolderIfNotExists() {
        // check if the folder is present
        if (!root.exists()) {
            root.mkdir();
            data = createFile();
            writeFile(new LocalUser());
        }else{
            data = createFile();
        }
    }

    private File createFile() {
        return new File(root, dataFileName);
    }

    // writing in json file stored inside the phone
    public void writeFile(LocalUser user) {

        // write new data in the local file in JSON format (pretty-printed)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userToJson = gson.toJson(user);  // mapping the user data into a String

        try {
            // put the String evaluated before to rewrite the file
            FileWriter fw = new FileWriter(data);
            fw.write(userToJson);
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // generate the String containing the reading of user's local file
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

    // mapping the JSON string evaluated in the previous functions as a LocalUser object
    public LocalUser getUser(){
        StringBuilder sb = this.generateStringBuilderData();
        Gson gson = new GsonBuilder().create();
        LocalUser user = gson.fromJson(sb.toString(), LocalUser.class);
        return user;
    }

    // easy getters
    public String readName(){
        return getUser().getName();
    }
    public String readID(){
        return getUser().getId();
    }

    // setter for changing the user's nickname
    public void changeUsername (String username){
        LocalUser me = getUser();
        me.setName(username);
        writeFile(me);
    }

    // functions to manage score of user
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
