package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.DB;
import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import java.sql.Timestamp;
import java.util.*;


public class Main{
    public static void main(String[] args) {

        // staticFiles.externalLocation("public");
        // http://sparkjava.com/documentation
        port(1234);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("REST2");
        MongoCollection<Document> usersCollection = db.getCollection("users");
        MongoCollection<Document> authCollection = db.getCollection("auth");

        get("/hello", (req, res) -> "Hello World");

        get("/login", (request, response) -> {
            String un = request.queryParams("username");
            String pw = request.queryParams("password");
            Document user = usersCollection.find(eq("username", un)).first();
            String rightPassword = user.getString("password");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Document token = new Document();
            if (pw.equals(rightPassword)) {
                token.append("username", un);
                String currentTime = Long.toString(timestamp.getTime());
                token.append("token", currentTime);
                authCollection.insertOne(token);
                return "Token = " + currentTime;
            }else {
                return "login_failed";
            }
        });

        get("/newuser", (req, res)-> {
            String username = req.queryParams("username");
            System.out.print(username);
            String password = req.queryParams("password");
            Document dc = new Document("username", username);
            dc.append("username", username).append("password", password);
            usersCollection.insertOne(dc);
            return "New User Created: " + "Username: " + username + " Password: " + password;
        });

        get("/addfriend", (req, res)-> {
            String friendToken = req.queryParams("token");
            String reqFriendUserID = req.queryParams("friendsuserid")
            ObjectId searchToken = new ObjectId(friendToken);
            Document searchID = usersCollection.find(eq("_id", searchToken)).first();
            String trueID = searchID.getString("_id");
            if(trueID.equals(reqFriendUserID)) {
                //insert friend's user ID into requested user's friend_ids list
                //needs to be tested and probably refined
                Document friendID = new Document();
                friendID.append("friend_ids", trueID);
                usersCollection.insertOne(friendID);
                return "Friend added successfully";
            } else {
                return "Bad token or friend ID";
            }
            //check auth collection for valid Token
            //if token is valid, we search the username key associated with the token
            // add the user to the requested user's friend list by ID-Name
        });

        get("/friends", (req, res)-> {
            String token = req.queryParams("token");
            ObjectId searchToken = new ObjectId(token);
            Document searchID = usersCollection.find(eq("_id", searchToken)).first();
            if(searchID != null){
                Object matchedFriend = searchID.get("friend");
                return matchedFriend;
            }
            else{ return "Bad token.";}
        });

    }
}
