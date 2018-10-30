package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;
import java.io.*;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Timestamp;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        // staticFiles.externalLocation("public");
        // http://sparkjava.com/documentation
        port(1234);
        // calling get will make your app start listening for the GET path with the /hello endpoint

        //MongoDB
        MongoClient mongoClient;
        mongoClient = MongoClients.create();
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

    }
}
