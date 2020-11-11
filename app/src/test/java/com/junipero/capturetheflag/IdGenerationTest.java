package com.junipero.capturetheflag;

import org.junit.Test;

import java.util.Calendar;
import java.util.Random;

public class IdGenerationTest {
    // made by Mario
    @Test
    public void generatePlayerIdTest() {
        StringBuilder millis = new StringBuilder();
        int k;

        millis.append(Calendar.getInstance().getTimeInMillis());
        k = new Random().nextInt(Integer.MAX_VALUE - 1000000000);
        millis.append(k + 1000000000);

        System.out.println(millis.toString());
    }

    @Test
    public void generateMatchIdTest(){
        StringBuilder id = new StringBuilder();
        Random r = new Random();
        int i, k, casual;

        for ( i = 0, k = r.nextInt(36); i < 4; i++, k = r.nextInt(36)){

            casual = k;
            id.append( (casual < 10) ? casual : Character.toString((char) (casual - 10 + 'a')));

        }

        System.out.println(id.toString());
    }


}
