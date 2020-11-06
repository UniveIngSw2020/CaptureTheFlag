package com.junipero.capturetheflag;


import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StoredDataManager {

    private File root;
    private final String folder = "userData";
    private final String data = "data";

    public StoredDataManager(File root) {
        this.root = new File(root, folder);
        createFolderIfNotExists();
    }

    private void createFolderIfNotExists() {
        if (!root.exists()) {
            root.mkdir();
            //writeFile(createFileIfNotExists());
        }
        writeFile(createFile());
    }

    private File createFile() {
        return new File(root, data);
    }

    private void writeFile(File data) {
        try {
            FileWriter fw = new FileWriter(data);
            // can be used append() or write()
            //fw.append("Ciao ho scritto forse la mia prima riga in un file dentro Android");

            fw.write("ID: \nName: \nWins: \nLosts: \nTies: \n" );
            fw.flush();
            fw.close();
            //output.setText(readFile());
            //Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder readFile(){
        File file = new File(root, data);
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


}
