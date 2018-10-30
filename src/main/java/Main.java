package main.java;

import static spark.Spark.*;
import java.io.*;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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


    }
}
