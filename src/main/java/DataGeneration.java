import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataGeneration {

    private static final String CLIENTS_JSON_PATH = "path/to/clients.json";
    private static final String COMPTES_JSON_PATH = "path/to/comptes.json";
    private static final String OPERATIONS_JSON_PATH = "path/to/operations.json";
    private static final String AGENCES_JSON_PATH = "path/to/agences.json";
    private static final String EMPLOYES_JSON_PATH = "path/to/employes.json";

    // Méthodes de génération de données pour chaque collection

    private static List<Document> generateClientsData() throws IOException {
        return readDocumentsFromJsonFile(CLIENTS_JSON_PATH);
    }

    private static List<Document> generateComptesData() throws IOException {
        return readDocumentsFromJsonFile(COMPTES_JSON_PATH);
    }

    private static List<Document> generateOperationsData() throws IOException {
        return readDocumentsFromJsonFile(OPERATIONS_JSON_PATH);
    }

    private static List<Document> generateAgencesData() throws IOException {
        return readDocumentsFromJsonFile(AGENCES_JSON_PATH);
    }

    private static List<Document> generateEmployesData() throws IOException {
        return readDocumentsFromJsonFile(EMPLOYES_JSON_PATH);
    }

    // Méthode utilitaire pour lire les documents à partir d'un fichier JSON

    private static List<Document> readDocumentsFromJsonFile(String filePath) throws IOException {
        File file = new File(filePath);
        List<Document> documents = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Document document = Document.parse(line);
                    documents.add(document);
                }
            }
        }

        return documents;
    }


    public static void main(String[] args) {
        try {
            List<Document> clients = generateClientsData();
            List<Document> comptes = generateComptesData();
            List<Document> operations = generateOperationsData();
            List<Document> agences = generateAgencesData();
            List<Document> employes = generateEmployesData();

            // Connexion à la base de données MongoDB
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase database = mongoClient.getDatabase("your_database_name");

            // Insérer les documents dans les collections correspondantes
            MongoCollection<Document> clientsCollection = database.getCollection("Clients");
            clientsCollection.insertMany(clients);

            MongoCollection<Document> comptesCollection = database.getCollection("Comptes");
            comptesCollection.insertMany(comptes);

            MongoCollection<Document> operationsCollection = database.getCollection("Operations");
            operationsCollection.insertMany(operations);

            MongoCollection<Document> agencesCollection = database.getCollection("Agences");
            agencesCollection.insertMany(agences);

            MongoCollection<Document> employesCollection = database.getCollection("Employes");
            employesCollection.insertMany(employes);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}