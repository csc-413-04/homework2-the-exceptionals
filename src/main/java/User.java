package main.java;

import java.util.ArrayList;

public class User {
    private String userName;
    private String password;
    private ArrayList<User> friends;

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
        friends = new ArrayList<User>();
    }

    public boolean checkPassword(String password){
        if (this.password == password)
            return true;
        return false;
    }

    public void addFriend(User newFriend){
        this.friends.add(newFriend);
    }

    public User findFriend(int token){
        return this.friends.get(token);
    }
}
