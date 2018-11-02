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
            String reqFriendUserID = req.queryParams("friend");
            Document auth = authCollection.find(eq("token", token)).first();
            if (auth!=null) {
                String reqUsername = auth.getString("username");
                Document user = usersCollection.find(eq("username", reqUsername)).first();
                String rightUsername = user.getString("username");
                String password = user.getString("password");
                usersCollection.findOneAndDelete(user);
                Document user2 = new Document();
                user2.append("username", rightUsername);
                user2.append("password", password);
                user2.append("friends", reqFriendUserID);
                usersCollection.insertOne(user2);
                return "Friend added successfully";
            }
            else{
                return "failed authentication";
            }
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
