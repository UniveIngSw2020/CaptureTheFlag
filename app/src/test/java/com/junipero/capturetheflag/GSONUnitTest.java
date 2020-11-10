package com.junipero.capturetheflag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GSONUnitTest {
    @Test
    public void GSONWritingTest() {
        LocalUser user1 = new LocalUser();
        user1.setId("@ID-1");
        user1.setName("Nasi");
        user1.setWins(13);
        user1.setLosts(3);
        user1.setTies(4);

        LocalUser user2 = new LocalUser();
        user2.setId("@ID-2");
        user2.setName("Simone");
        user2.setWins(53);
        user2.setLosts(34);
        user2.setTies(21);

        List<LocalUser> users = new ArrayList<LocalUser>();
        users.add(user1);
        users.add(user2);

        Gson gson = new Gson();
        // print the JSON of first user in a String type
        String user1Json = gson.toJson(user1);
        System.out.println("FIRST USERS JSON FILE:\n" + user1Json + "\n");
        // print the JSON of second user in a String type
        String user2Json = gson.toJson(user2);
        System.out.println("SECOND USERS JSON FILE:\n" + user2Json + "\n" );

        // print the collection of users in JSON format in a String type
        String usersJson = gson.toJson(users);
        System.out.println("COLLECTION OF USERS IN JSON FORMAT:\n" + usersJson);
    }

    @Test
    public void GSONReadingTest (){
        String jsonUser = "{\"id\":\"@ID-1\",\"name\":\"Nasi\",\"wins\":13,\"losts\":3,\"ties\":4}";

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        // show the retrieved data of a user using toString of UserData class
        LocalUser user = gson.fromJson(jsonUser, LocalUser.class);
        System.out.println(user);

        //other parameters should be retrieved using the getter function of the class specified
        // i.e.
        System.out.println(user.getId() + "\n"
            + user.getName() + "\n"
            + user.getWins()
        );

        // increment a int parameter of the user class
        user.setWins(user.getWins()+1);
        System.out.println(user.getWins());


    }
}
