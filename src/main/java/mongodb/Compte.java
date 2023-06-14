package mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class Compte {
    private String id;
    private Client client;
    private String IBAN;
    private String BIC;
    private String typeCompte;
    private double solde;

    // Constructeur
    public Compte(Client client, String typeCompte, double solde, String IBAN, String BIC) {
        this.client = client;
        this.typeCompte = typeCompte;
        this.solde = solde;
        this.IBAN = IBAN;
        this.BIC = BIC;
    }

    // Getters et setters pour toutes les propriétés

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getBIC() {
        return BIC;
    }

    public void setBIC(String BIC) {
        this.BIC = BIC;
    }

    // Méthodes CRUD

    public void insertCompte() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Document document = new Document();
        document.append("clientId", this.client.getId());
        document.append("typeCompte", this.typeCompte);
        document.append("solde", this.solde);
        document.append("IBAN", this.IBAN);
        document.append("BIC", this.BIC);

        collection.insertOne(document);
    }

    public void updateCompte() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("clientId", this.client.getId())
                .append("typeCompte", this.typeCompte)
                .append("IBAN", this.IBAN)
                .append("BIC", this.BIC)
                .append("solde", this.solde));

        collection.updateOne(filter, update);
    }

    public void deleteCompte() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    // Méthodes de recherche et de récupération de comptes

    public static Compte getCompteById(String compteId) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Document filter = new Document("_id", compteId);
        Document result = collection.find(filter).first();

        if (result != null) {
            Compte compte = new Compte(Client.getClientById(result.getString("clientId")),
                    result.getString("typeCompte"), result.getDouble("solde"), result.getString("IBAN"), result.getString("BIC"));
            compte.setId(result.getString("_id"));
            return compte;
        }

        return null;
    }

    // Méthode pour créer un index secondaire sur le champ "clientId"
    public static void createClientIdIndex() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        IndexOptions indexOptions = new IndexOptions().unique(false);
        collection.createIndex(new Document("clientId", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getComptesWithClients() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> comptesCollection = database.getCollection("Comptes");
        MongoCollection<Document> clientsCollection = database.getCollection("Clients");

        List<Document> comptesWithClients = new ArrayList<>();

        MongoCursor<Document> comptesCursor = comptesCollection.find().iterator();
        while (comptesCursor.hasNext()) {
            Document compte = comptesCursor.next();
            String clientId = compte.getString("clientId");

            Document client = clientsCollection.find(new Document("_id", clientId)).first();
            compte.append("client", client);

            comptesWithClients.add(compte);
        }

        return comptesWithClients;
    }

    // Groupement
    public static List<Document> getComptesByType() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Bson group = Aggregates.group("$type", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }

    // Tri
    public static List<Document> getComptesSortedBySolde() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        FindIterable<Document> result = collection.find().sort(Sorts.descending("solde"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void updateSoldeInBulk(double amount) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Comptes");

        Bson update = Updates.inc("solde", amount);

        collection.updateMany(new Document(), update);
    }

    // Méthodes utilitaires

    private static List<Document> toList(MongoIterable<Document> iterable) {
        List<Document> list = new ArrayList<>();
        iterable.into(list);
        return list;
    }
}
