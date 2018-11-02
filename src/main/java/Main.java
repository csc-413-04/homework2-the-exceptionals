package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.DB;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import com.mongodb.MongoClient;
import org.bson.conversions.Bson;
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
            String token = req.queryParams("token");
            ObjectId searchToken = new ObjectId(token);
            Document validateToken = authCollection.find(eq("token", searchToken)).first();
            String tokenString = validateToken.getString("token");
            if(tokenString.equals(token)) { //checks to see if given token is valid
                //gets our current username by their token
                Document authenticatedUser = authCollection.find(eq("username", token)).first();
                String usersID = req.queryParams("friendsuserid");
                ObjectId userId = new ObjectId(usersID);
                Document findRequestedUser = usersCollection.find(new Document("_id", userId)).first();
                Document newFriend = new Document().append("friend_id", findRequestedUser);
                usersCollection.updateOne(eq("_id", authenticatedUser), Updates.addToSet("friend_ids", newFriend);
                return "Friend added successfully";
            } else {
                return "Bad token";
            }
            //check auth collection for valid Token
            //if token is valid, we search the username key associated with the token
            // add the user to the requested user's friend list by ID-Name
        });

        get("/friends", (req, res)-> {
            String token = req.queryParams("token");
            Document auth = authCollection.find(eq("token", token)).first();
            String reqUsername = auth.getString("username");
            Document user = usersCollection.find(eq("username", reqUsername)).first();
            String otherfriendsid = user.getString("friends");
            String password = user.getString("password");
            usersCollection.findOneAndDelete(user);
            Document user2 = new Document();
            user2.append("username", reqUsername);
            user2.append("password", password);
            user2.append("friends", otherfriendsid);
            usersCollection.insertOne(user2);
            return otherfriendsid;
        });

    }
}
