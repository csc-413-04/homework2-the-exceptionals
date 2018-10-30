package main.java;
import static spark.Spark.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.DB;
import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.MongoClient;

public class Main {



    public static void main(String[] args) {

        // staticFiles.externalLocation("public");
        // http://sparkjava.com/documentation
        port(1234);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("REST2");
        MongoCollection<Document> usersCollection = db.getCollection("users");
        MongoCollection<Document> authCollection = db.getCollection("auth");

        // calling get will make your app start listening for the GET path with the /hello endpoint
        get("/hello", (req, res) -> "Hello World");


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
