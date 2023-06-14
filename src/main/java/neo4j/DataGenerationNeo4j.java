package neo4j;

import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataGenerationNeo4j {

    private static final String CLIENTS_CSV_PATH = "src/main/resources/client.csv";
    private static final String COMPTES_CSV_PATH = "src/main/resources/compte.csv";
    private static final String OPERATIONS_CSV_PATH = "src/main/resources/operation.csv";
    private static final String AGENCES_CSV_PATH = "src/main/resources/agence.csv";
    private static final String EMPLOYES_CSV_PATH = "src/main/resources/employe.csv";

    // Méthodes de génération de données pour chaque collection

    private static List<String[]> generateClientsData() throws IOException {
        return readDataFromCsvFile(CLIENTS_CSV_PATH);
    }

    private static List<String[]> generateComptesData() throws IOException {
        return readDataFromCsvFile(COMPTES_CSV_PATH);
    }

    private static List<String[]> generateOperationsData() throws IOException {
        return readDataFromCsvFile(OPERATIONS_CSV_PATH);
    }

    private static List<String[]> generateAgencesData() throws IOException {
        return readDataFromCsvFile(AGENCES_CSV_PATH);
    }

    private static List<String[]> generateEmployesData() throws IOException {
        return readDataFromCsvFile(EMPLOYES_CSV_PATH);
    }

    // Méthode utilitaire pour lire les données à partir d'un fichier CSV

    private static List<String[]> readDataFromCsvFile(String filePath) throws IOException {
        File file = new File(filePath);
        List<String[]> data = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(",");
                    data.add(row);
                }
            }
        }

        return data;
    }

    public static void main(String[] args) {
        try {
            List<String[]> clients = generateClientsData();
            List<String[]> comptes = generateComptesData();
            List<String[]> operations = generateOperationsData();
            List<String[]> agences = generateAgencesData();
            List<String[]> employes = generateEmployesData();

            // Connexion à la base de données Neo4j
            Driver driver = GraphDatabase.driver(
                    "neo4j+s://" + System.getenv("760dbeec.databases.neo4j.io"),
                    AuthTokens.basic(System.getenv("neo4j"), System.getenv("77yxPdT-1WryEoPehgpVwsMbpEnOQCoK_cRABjuvA_E"))
            );
            Session session = driver.session();

            // Insérer les données dans la base de données
            insertData(session, "CREATE (:mongodb.Client {id: $id, name: $name, age: $age})",
                    clients, new String[]{"id", "name", "age"});
            insertData(session, "CREATE (:mongodb.Compte {id: $id, balance: $balance, clientId: $clientId})",
                    comptes, new String[]{"id", "balance", "clientId"});
            insertData(session, "CREATE (:mongodb.Operation {id: $id, description: $description, compteId: $compteId})",
                    operations, new String[]{"id", "description", "compteId"});
            insertData(session, "CREATE (:mongodb.Agence {id: $id, name: $name, location: $location})",
                    agences, new String[]{"id", "name", "location"});
            insertData(session, "CREATE (:mongodb.Employe {id: $id, name: $name, agenceId: $agenceId})",
                    employes, new String[]{"id", "name", "agenceId"});

            session.close();
            driver.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void insertData(Session session, String query, List<String[]> data, String[] parameters) {
        for (String[] row : data) {
            Value parametersValue = parameters(
                    parameters[0], row[0],
                    parameters[1], row[1],
                    parameters[2], row[2]
            );
            session.run(query, parametersValue);
        }
    }
}
