package mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

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
    public void insertClient() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Document document = new Document();
        document.append("nom", this.nom);
        document.append("prenom", this.prenom);
        document.append("adresse", this.adresse);
        document.append("numeroTelephone", this.numeroTelephone);
        document.append("dateNaissance", this.dateNaissance);
        document.append("email", this.email);

        collection.insertOne(document);
    }

    public void updateClient() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("nom", this.nom)
                .append("prenom", this.prenom)
                .append("adresse", this.adresse)
                .append("numeroTelephone", this.numeroTelephone)
                .append("dateNaissance", this.dateNaissance)
                .append("email", this.email));

        collection.updateOne(filter, update);
    }

    public void deleteClient() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    public static Client getClientById(String clientId) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Document filter = new Document("_id", clientId);
        Document result = collection.find(filter).first();

        if (result != null) {
            Client client = new Client(result.getString("nom"), result.getString("prenom"),
                    result.getString("adresse"), result.getString("numeroTelephone"),
                    result.getString("dateNaissance"), result.getString("email"));
            client.setId(result.getString("_id"));
            return client;
        }

        return null;
    }

    public static Client getClientByEmail(String email) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Document filter = new Document("email", email);
        Document result = collection.find(filter).first();

        if (result != null) {
            Client client = new Client(result.getString("nom"), result.getString("prenom"),
                    result.getString("adresse"), result.getString("numeroTelephone"),
                    result.getString("dateNaissance"), result.getString("email"));
            client.setId(result.getString("_id"));
            return client;
        }

        return null;
    }

    // Méthode pour créer un index secondaire sur le champ "email"
    public static void createEmailIndex() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        IndexOptions indexOptions = new IndexOptions().unique(true);
        collection.createIndex(new Document("email", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getClientsWithComptes() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> clientsCollection = database.getCollection("Clients");
        MongoCollection<Document> comptesCollection = database.getCollection("Comptes");

        List<Document> clientsWithComptes = new ArrayList<>();

        MongoCursor<Document> clientsCursor = clientsCollection.find().iterator();
        while (clientsCursor.hasNext()) {
            Document client = clientsCursor.next();
            String clientId = client.getString("_id");

            FindIterable<Document> comptes = comptesCollection.find(new Document("clientId", clientId));
            client.append("comptes", toList(comptes));

            clientsWithComptes.add(client);
        }

        return clientsWithComptes;
    }

    // Groupement
    public static List<Document> getClientsByAgeGroup() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Bson group = Aggregates.group("$ageGroup", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }

    // Tri
    public static List<Document> getClientsSortedByNom() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        FindIterable<Document> result = collection.find().sort(Sorts.ascending("nom"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void updateEmailInBulk(String newEmail) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Clients");

        Bson update = Updates.set("email", newEmail);

        collection.updateMany(new Document(), update);
    }

    // Méthodes utilitaires

    private static List<Document> toList(MongoIterable<Document> iterable) {
        List<Document> list = new ArrayList<>();
        iterable.into(list);
        return list;
    }
}
