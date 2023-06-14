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
    private String telephone;
    private String dateNaissance;
    private String email;
    private List<Compte> comptes;
    private Agence agence;

    // Constructeur
    public Client(String nom, String prenom, String adresse, String telephone, String dateNaissance, String email, Agence agence) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.comptes = new ArrayList<>();
        this.agence = agence;
    }

    // Getters et setters pour toutes les propriétés

    public String getId() {
        return this.id;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    // Méthodes CRUD
    public void insertClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "CREATE (c:Client {nom: $nom, prenom: $prenom, adresse: $adresse, telephone: $telephone, dateNaissance: $dateNaissance, email: $email, agence: $agence}) RETURN c";
            Value parameters = Values.parameters(
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "dateNaissance", this.dateNaissance,
                    "email", this.email,
                    "agence", this.agence.getId()
            );

            Result result = session.run(query, parameters);
            if (result.hasNext()) {
                Record record = result.next();
                Node createdNode = record.get("c").asNode();
                String clientId = String.valueOf(createdNode.id());
                this.setId(clientId);
            }
        }
    }

    public void updateClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE ID(c) = " + this.id + " SET c.nom = $nom, c.prenom = $prenom, c.adresse = $adresse, c.telephone = $telephone, c.dateNaissance = $dateNaissance, c.email = $email";
            Value parameters = Values.parameters(
                    "id", this.id,
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "dateNaissance", this.dateNaissance,
                    "email", this.email
            );

            session.run(query, parameters);
        }
    }

    public void deleteClient(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE ID(c) = " + this.id + " DETACH DELETE c";
            Value parameters = Values.parameters("id", this.id);

            session.run(query, parameters);
        }
    }

    public static Client getClientById(Driver driver, String clientId) {
        try (Session session = driver.session()) {
            String query = "MATCH (c:Client) WHERE ID(c) = " + clientId + " RETURN c";
            Value parameters = Values.parameters("id", Long.parseLong(clientId));

            Result result = session.run(query, parameters);
            if (result.hasNext()) {
                Record record = result.next();
                Node clientNode = record.get("c").asNode();

                Client client = new Client(
                        clientNode.get("nom").asString(),
                        clientNode.get("prenom").asString(),
                        clientNode.get("adresse").asString(),
                        clientNode.get("telephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString(),
                        Agence.getAgenceById(driver, clientNode.get("agence").asString())
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
                        clientNode.get("telephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString(),
                        Agence.getAgenceById(driver, clientNode.get("agence").asString())
                );
                client.setId(String.valueOf(clientNode.id()));
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
                        clientNode.get("telephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString(),
                        Agence.getAgenceById(driver, clientNode.get("agence").asString())
                );
                client.setId(String.valueOf(clientNode.id()));

                // Vous pouvez traiter les comptes ici et les ajouter au client si nécessaire
                clientsWithComptes.add(client);
            }

            return clientsWithComptes;
        }
    }

    // Groupement
//    public static List<Client> getClientsByAgeGroup(Driver driver) {
//        try (Session session = driver.session()) {
//            String query = "MATCH (c:Client) RETURN c.ageGroup AS ageGroup, count(*) AS count ORDER BY ageGroup ASC";
//            Result result = session.run(query);
//
//            List<Client> clientsByAgeGroup = new ArrayList<>();
//
//            while (result.hasNext()) {
//                Record record = result.next();
//                String ageGroup = record.get("ageGroup").asString();
//                long count = record.get("count").asLong();
//
//                // Créez un objet Client avec les informations de groupe d'âge et de compte ici
//                Client client = new Client();
//                // Définissez les propriétés du client en fonction des données de groupe d'âge et de compte
//                // client.setAgeGroup(ageGroup);
//                // client.setCount(count);
//
//                clientsByAgeGroup.add(client);
//            }
//
//            return clientsByAgeGroup;
//        }
//    }

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
                        clientNode.get("telephone").asString(),
                        clientNode.get("dateNaissance").asString(),
                        clientNode.get("email").asString(),
                        Agence.getAgenceById(driver, clientNode.get("agence").asString())
                );
                client.setId(String.valueOf(clientNode.id()));

                clientsSortedByNom.add(client);
            }

            return clientsSortedByNom;
        }
    }

    // Méthodes utilitaires
}
