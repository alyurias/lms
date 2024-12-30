
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBController {

    private static final String CONNECTION_STRING = "mongodb+srv://lejla7dedic:OylYqRc3RB7nuD28@cluster0.4nslb.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static MongoDBController instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBController() {
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("radnici");

            // Test connection
            database.runCommand(new Document("ping", 1));
            System.out.println("Successfully connected to MongoDB!");

        } catch (MongoException e) {
            e.printStackTrace();
            throw new RuntimeException("MongoDB connection failed");
        }
    }

    public static synchronized MongoDBController getInstance() {
        if (instance == null) {
            instance = new MongoDBController();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}