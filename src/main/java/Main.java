package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.DB;
import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.MongoClient;

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


    }
}
