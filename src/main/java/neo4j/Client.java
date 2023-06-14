package neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String id;
    private String nom;
    private String prenom;
    private String adresse;
    private String numeroTelephone;
    private String dateNaissance;
    private String email;

    // Constructeur
    public Client(String nom, String prenom, String adresse, String numeroTelephone, String dateNaissance, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.numeroTelephone = numeroTelephone;
        this.dateNaissance = dateNaissance;
        this.email = email;
    }

    // Getters et setters pour toutes les propriétés
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Méthodes CRUD
    public void insertClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "CREATE (c:Client {nom: $nom, prenom: $prenom, adresse: $adresse, numeroTelephone: $numeroTelephone, dateNaissance: $dateNaissance, email: $email}) RETURN c.id AS id";
            Value parameters = Values.parameters(
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "numeroTelephone", this.numeroTelephone,
                    "dateNaissance", this.dateNaissance,
                    "email", this.email
            );

            Result result = session.run(query, parameters);
            if (result.hasNext()) {
                Record record = result.next();
                this.id = record.get("id").asString();
            }
        }
    }

    public void updateClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE c.id = $id " +
                    "SET c.nom = $nom, c.prenom = $prenom, c.adresse = $adresse, c.numeroTelephone = $numeroTelephone, c.dateNaissance = $dateNaissance, c.email = $email";
            Value parameters = Values.parameters(
                    "id", this.id,
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "numeroTelephone", this.numeroTelephone,
                    "dateNaissance", this.dateNaissance,
                    "email", this.email
            );

            session.run(query, parameters);
        }
    }

    public void deleteClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE c.id = $id DETACH DELETE c";
            Value parameters = Values.parameters("id", this.id);

            session.run(query, parameters);
        }
    }

    public static Client getClientById(Driver driver, String clientId) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE c.id = $id RETURN c";
            Value parameters = Values.parameters("id", clientId);

            Result result = session.run(query, parameters);
            if (result.hasNext()) {
                Record record = result.next();
                Node clientNode = record.get("c").asNode();

                Client client = new Client(
                        clientNode.get("nom").asString(),
                        clientNode.get("prenom").asString(),
                        clientNode.get("adresse").asString(),
                        clientNode.get("numeroTelephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString()
                );
                client.setId(clientId);
                return client;
            }
        }

        return null;
    }

    public static Client getClientByEmail(Driver driver, String email) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE c.email = $email RETURN c";
            Value parameters = Values.parameters("email", email);

            Result result = session.run(query, parameters);
            if (result.hasNext()) {
                Record record = result.next();
                Node clientNode = record.get("c").asNode();

                Client client = new Client(
                        clientNode.get("nom").asString(),
                        clientNode.get("prenom").asString(),
                        clientNode.get("adresse").asString(),
                        clientNode.get("numeroTelephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString()
                );
                client.setId(clientNode.get("id").asString());
                return client;
            }
        }

        return null;
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Client> getClientsWithComptes(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client)-[:HAS_COMPTE]->(compte) RETURN c, collect(compte) AS comptes";
            Result result = session.run(query);

            List<Client> clientsWithComptes = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                Node clientNode = record.get("c").asNode();

                List<Node> compteNodes = record.get("comptes").asList(Value::asNode);

                Client client = new Client(
                        clientNode.get("nom").asString(),
                        clientNode.get("prenom").asString(),
                        clientNode.get("adresse").asString(),
                        clientNode.get("numeroTelephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString()
                );
                client.setId(clientNode.get("id").asString());

                // Vous pouvez traiter les comptes ici et les ajouter au client si nécessaire
                clientsWithComptes.add(client);
            }

            return clientsWithComptes;
        }
    }

    // Groupement
    public static List<Client> getClientsByAgeGroup(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) RETURN c.ageGroup AS ageGroup, count(*) AS count ORDER BY ageGroup ASC";
            Result result = session.run(query);

            List<Client> clientsByAgeGroup = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                String ageGroup = record.get("ageGroup").asString();
                long count = record.get("count").asLong();

                // Créez un objet Client avec les informations de groupe d'âge et de compte ici
                Client client = new Client();
                // Définissez les propriétés du client en fonction des données de groupe d'âge et de compte
                // client.setAgeGroup(ageGroup);
                // client.setCount(count);

                clientsByAgeGroup.add(client);
            }

            return clientsByAgeGroup;
        }
    }

    // Tri
    public static List<Client> getClientsSortedByNom(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) RETURN c ORDER BY c.nom ASC";
            Result result = session.run(query);

            List<Client> clientsSortedByNom = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                Node clientNode = record.get("c").asNode();

                Client client = new Client(
                        clientNode.get("nom").asString(),
                        clientNode.get("prenom").asString(),
                        clientNode.get("adresse").asString(),
                        clientNode.get("numeroTelephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString()
                );
                client.setId(clientNode.get("id").asString());

                clientsSortedByNom.add(client);
            }

            return clientsSortedByNom;
        }
    }

    // Méthodes utilitaires

    private static List<Client> toList(Result result) {
        List<Client> list = new ArrayList<>();

        while (result.hasNext()) {
            Record record = result.next();
            Node clientNode = record.get("c").asNode();

            Client client = new Client(
                    clientNode.get("nom").asString(),
                    clientNode.get("prenom").asString(),
                    clientNode.get("adresse").asString(),
                    clientNode.get("numeroTelephone").asString(),
                    clientNode.get("dateNaissance").asString(),
                    clientNode.get("email").asString()
            );
            client.setId(clientNode.get("id").asString());

            list.add(client);
        }

        return list;
    }
}
