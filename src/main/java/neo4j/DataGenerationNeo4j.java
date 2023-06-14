package neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String uri = "neo4j+s://91d8e935.databases.neo4j.io";
        String user = "neo4j";
        String password = "K01Vs880wyeLSSe6kJXXS2eX93c1Inos9W7ZV3PAFi0";

        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

        try (Session session = driver.session()) {
            List<String[]> clients = generateClientsData();
            List<String[]> comptes = generateComptesData();
            List<String[]> operations = generateOperationsData();
            List<String[]> agences = generateAgencesData();
            List<String[]> employes = generateEmployesData();

            // Insérer les données des employés
            insertData(session, "CREATE (e:Employe {id: $id, nom: $nom, prenom: $prenom, adresse: $adresse, telephone: $telephone, email: $email, " +
                    "dateNaissance: $dateNaissance, dateEmbauche: $dateEmbauche, salaire: $salaire, agence: $agence})",
                    employes, new String[]{"id", "nom", "prenom", "adresse", "telephone", "email", "dateNaissance", "dateEmbauche", "salaire", "agence"});


            // Insérer les données des clients
            // Insérer les données des clients
            insertData(session, "CREATE (c:Client {id: $id, nom: $nom, prenom: $prenom, adresse: $adresse, telephone: $telephone, email: $email, " +
                    "dateNaissance: $dateNaissance, agence: $agence})", clients, new String[]{"id", "nom", "prenom", "adresse", "telephone", "email", "dateNaissance", "agence"});


            // Insérer les données des comptes
            insertData(session, "CREATE (cpt:Compte {id: $id, IBAN: $IBAN, BIC: $BIC, typeCompte: $typeCompte, solde: $solde, client: $client})", comptes,
                    new String[]{"id", "IBAN", "BIC", "typeCompte", "solde", "client"});


            // Insérer les données des opérations
            insertData(session, "CREATE (op:Operation {id: $id, client: $client, compte: $compte, date: $date, montant: $montant, typeOperation: $typeOperation})",
                    operations, new String[]{"id", "client", "compte", "date", "montant", "typeOperation"});


            // Insérer les données des agences
            insertData(session, "CREATE (a:Agence {id: $id, nom: $nom, adresse: $adresse, telephone: $telephone, email: $email})", agences,
                    new String[]{"id", "nom", "adresse", "telephone", "email"});



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        driver.close();
    }


    private static void insertData(Session session, String query, List<String[]> data, String[] parameters) {
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                continue; // Passe à l'itération suivante pour sauter la première ligne
            }

            String[] row = data.get(i);
            int parametersLength = parameters.length;
            Map<String, Object> parameterMap = new HashMap<>();

            for (int j = 0; j < parametersLength; j++) {
                parameterMap.put(parameters[j], row[j]);
            }

            session.run(query, parameterMap);
        }
    }


}