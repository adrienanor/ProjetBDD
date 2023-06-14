package mongodb;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Operation {
    private String id;
    private String clientId;
    private String compteId;
    private Date date;
    private double montant;
    private String typeOperation;

    // Constructeur
    public Operation(String clientId, String compteId, Date date, double montant, String typeOperation) {
        this.clientId = clientId;
        this.compteId = compteId;
        this.date = date;
        this.montant = montant;
        this.typeOperation = typeOperation;
    }

    // Getters et setters pour toutes les propriétés

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCompteId() {
        return compteId;
    }

    public void setCompteId(String compteId) {
        this.compteId = compteId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    // Méthodes CRUD

    public void insertOperation() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Document document = new Document();
        document.append("clientId", this.clientId);
        document.append("compteId", this.compteId);
        document.append("date", this.date);
        document.append("montant", this.montant);
        document.append("typeOperation", this.typeOperation);

        collection.insertOne(document);
    }

    public void updateOperation() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("clientId", this.clientId)
                .append("compteId", this.compteId)
                .append("date", this.date)
                .append("typeOperation", this.typeOperation)
                .append("montant", this.montant));

        collection.updateOne(filter, update);
    }

    public void deleteOperation() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    // Méthodes de recherche et de récupération d'opérations

    public static Operation getOperationById(String operationId) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Document filter = new Document("_id", operationId);
        Document result = collection.find(filter).first();

        if (result != null) {
            Operation operation = new Operation(result.getString("clientId"), result.getString("compteId"),
                    result.getDate("date"), result.getDouble("montant"), result.getString("typeOperation"));
            operation.setId(result.getString("_id"));
            return operation;
        }

        return null;
    }

    public static List<Operation> getOperationsByClientId(String clientId) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Document filter = new Document("clientId", clientId);
        List<Operation> operations = new ArrayList<>();

        collection.find(filter).forEach((Block<? super Document>) (Document document) -> {
            Operation operation = new Operation(document.getString("clientId"), document.getString("compteId"),
                    document.getDate("date"), document.getDouble("montant"), document.getString("typeOperation"));
            operation.setId(document.getString("_id"));
            operations.add(operation);
        });

        return operations;
    }

    // Méthode pour créer un index secondaire sur le champ "clientId"
    public static void createClientIdIndex() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        IndexOptions indexOptions = new IndexOptions().unique(false);
        collection.createIndex(new Document("clientId", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getOperationsWithClients() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> operationsCollection = database.getCollection("Operations");
        MongoCollection<Document> clientsCollection = database.getCollection("Clients");

        List<Document> operationsWithClients = new ArrayList<>();

        MongoCursor<Document> operationsCursor = operationsCollection.find().iterator();
        while (operationsCursor.hasNext()) {
            Document operation = operationsCursor.next();
            String clientId = operation.getString("clientId");

            Document client = clientsCollection.find(new Document("_id", clientId)).first();
            operation.append("client", client);

            operationsWithClients.add(operation);
        }

        return operationsWithClients;
    }

    // Groupement
    public static List<Document> getOperationsByType() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Bson group = Aggregates.group("$type", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }


    // Tri
    public static List<Document> getOperationsSortedByMontant() {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        FindIterable<Document> result = collection.find().sort(Sorts.descending("montant"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void deleteOperationsByClientId(String clientId) {
        MongoDatabase database = MongoClients.create().getDatabase("your_database_name");
        MongoCollection<Document> collection = database.getCollection("Operations");

        Bson filter = Filters.eq("clientId", clientId);
        collection.deleteMany(filter);
    }

    // Méthodes utilitaires

    private static List<Document> toList(MongoIterable<Document> iterable) {
        List<Document> list = new ArrayList<>();
        iterable.into(list);
        return list;
    }
}
