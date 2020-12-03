package com.junipero.capturetheflag;

import java.util.Calendar;
import java.util.Random;

public final class IdGenerator {
    // made by Mario

    public static String generatePlayerId() {
        StringBuilder millis = new StringBuilder();
        int k;

        millis.append(Calendar.getInstance().getTimeInMillis());
        k = new Random().nextInt(Integer.MAX_VALUE - 1000000000);
        millis.append(k + 1000000000);

        return millis.toString();
    }

    public static String generateMatchId(){
        StringBuilder id = new StringBuilder();
        Random r = new Random();
        int i, k, casual;

        for ( i = 0, k = r.nextInt(36); i < 4; i++, k = r.nextInt(36)){
            casual = k;
            // need to correct the cast on the next line
            id.append( (casual < 10) ? casual : Character.toString((char) (casual - 10 + 'a')));
        }

        return id.toString();
    }




}
