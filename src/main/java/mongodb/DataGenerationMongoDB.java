package mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertManyOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataGenerationMongoDB {

    private static final String CLIENTS_CSV_PATH = "src/main/resources/client.csv";
    private static final String COMPTES_CSV_PATH = "src/main/resources/compte.csv";
    private static final String OPERATIONS_CSV_PATH = "src/main/resources/operation.csv";
    private static final String AGENCES_CSV_PATH = "src/main/resources/agence.csv";
    private static final String EMPLOYES_CSV_PATH = "src/main/resources/employe.csv";

    private static void insertDataFromCSV(String filename, MongoCollection<Document> collection) {
        try (CSVParser csvParser = new CSVParser(new FileReader(filename), CSVFormat.DEFAULT.withHeader())) {
            List<Document> documents = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                // Create a document for each CSV record
                Document document = new Document();
                for (String header : csvParser.getHeaderMap().keySet()) {
                    String value = record.get(header);

                    if (Objects.equals(header, "_id"))
                        document.append(header, new ObjectId(value));
                    else
                        document.append(header, value);
                }
                documents.add(document);
            }

            // Insert the documents into the collection
            collection.insertMany(documents, new InsertManyOptions().ordered(false));
            System.out.println("Inserted " + documents.size() + " documents into collection: " + collection.getNamespace().getCollectionName());
        } catch (Exception e) {
            System.err.println("Error inserting data from CSV: " + e.getMessage());
        }
    }


    public static void main(String[] args) {

        try {
            ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .retryWrites(true)
                    .build();

            MongoClient client = MongoClients.create(settings);

            System.out.println("Connecté avec succès à la base de données MongoDB dans le cloud!");

            // Utilisation de la connexion pour effectuer des opérations sur la base de données

            MongoDatabase database = client.getDatabase("projetBDD");

            //Insérer les documents dans les collections correspondantes
            MongoCollection<Document> clientsCollection = database.getCollection("client");

            MongoCollection<Document> comptesCollection = database.getCollection("compte");

            MongoCollection<Document> operationsCollection = database.getCollection("operation");

            MongoCollection<Document> agencesCollection = database.getCollection("agence");

            MongoCollection<Document> employesCollection = database.getCollection("employe");

            // Insert data from CSV files into collections
            insertDataFromCSV(CLIENTS_CSV_PATH, clientsCollection);
            insertDataFromCSV(COMPTES_CSV_PATH, comptesCollection);
            insertDataFromCSV(OPERATIONS_CSV_PATH, operationsCollection);
            insertDataFromCSV(AGENCES_CSV_PATH, agencesCollection);
            insertDataFromCSV(EMPLOYES_CSV_PATH, employesCollection);

            client.close(); // Fermer la connexion lorsque vous avez terminé
        } catch (MongoException e) {
            System.err.println("Erreur lors de la connexion à MongoDB : " + e.getMessage());
        }
    }
}