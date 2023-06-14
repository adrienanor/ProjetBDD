package mongodb;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Operation {
    private String id;
    private Client client;
    private Compte compte;
    private Date date;
    private double montant;
    private String typeOperation;

    // Constructeur
    public Operation(Client client, Compte compte, Date date, double montant, String typeOperation) {
        this.client = client;
        this.compte = compte;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
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
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Document document = new Document();
        document.append("client", new ObjectId(this.client.getId()));
        document.append("compte", new ObjectId(this.compte.getId()));
        document.append("date", this.date);
        document.append("montant", this.montant);
        document.append("typeOperation", this.typeOperation);

        collection.insertOne(document);

        this.setId(document.getObjectId("_id").toString());
    }

    public void updateOperation() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("clientId", this.client.getId())
                .append("compteId", this.compte.getId())
                .append("date", this.date)
                .append("typeOperation", this.typeOperation)
                .append("montant", this.montant));

        collection.updateOne(filter, update);
    }

    public void deleteOperation() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    // Méthodes de recherche et de récupération d'opérations

    public static Operation getOperationById(String operationId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Document result = collection.find(eq("_id", new ObjectId(operationId))).first();

        if (result != null) {
            Operation operation = new Operation(Client.getClientById(result.getObjectId("client").toString()), Compte.getCompteById(result.getObjectId("compte").toString()),
                    result.getDate("date"), result.getDouble("montant"), result.getString("typeOperation"));
            operation.setId(result.getObjectId("_id").toString());
            return operation;
        }

        return null;
    }

    public static List<Operation> getOperationsByClientId(String clientId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Document filter = new Document("clientId", clientId);
        List<Operation> operations = new ArrayList<>();

        collection.find(filter).forEach((Block<? super Document>) (Document document) -> {
            Operation operation = new Operation(Client.getClientById(document.getString("client")), Compte.getCompteById(document.getString("compte")),
                    document.getDate("date"), document.getDouble("montant"), document.getString("typeOperation"));
            operation.setId(document.getObjectId("_id").toString());
            operations.add(operation);
        });

        return operations;
    }

    // Méthode pour créer un index secondaire sur le champ "clientId"
    public static void createClientIdIndex() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        IndexOptions indexOptions = new IndexOptions().unique(false);
        collection.createIndex(new Document("clientId", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getOperationsWithClients() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> operationsCollection = database.getCollection("operation");
        MongoCollection<Document> clientsCollection = database.getCollection("client");

        List<Document> operationsWithClients = new ArrayList<>();

        MongoCursor<Document> operationsCursor = operationsCollection.find().iterator();
        while (operationsCursor.hasNext()) {
            Document operation = operationsCursor.next();
            String clientId = operation.getString("clientId");

            Document clientDoc = clientsCollection.find(new Document("_id", clientId)).first();
            operation.append("client", clientDoc);

            operationsWithClients.add(operation);
        }

        return operationsWithClients;
    }

    // Groupement
    public static List<Document> getOperationsByType() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        Bson group = Aggregates.group("$type", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }


    // Tri
    public static List<Document> getOperationsSortedByMontant() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

        FindIterable<Document> result = collection.find().sort(Sorts.descending("montant"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void deleteOperationsByClientId(String clientId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("operation");

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
