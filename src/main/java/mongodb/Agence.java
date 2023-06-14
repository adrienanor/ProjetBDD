package mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Agence {
    private ObjectId id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private List<Employe> employes;

    // Constructeur
    public Agence(String nom, String adresse, String telephone, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        employes = new ArrayList<Employe>();
    }

    // Getters et setters pour toutes les propriétés

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Employe> getEmployes() {
        return employes;
    }

    public void addEmploye(Employe employe) {
        employes.add(employe);
    }

    // Méthodes CRUD
    public void insertAgence() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Document document = new Document();
        document.append("nom", this.nom);
        document.append("adresse", this.adresse);
        document.append("telephone", this.telephone);
        document.append("email", this.email);

        collection.insertOne(document);

        this.setId(document.getObjectId("_id"));
    }

    public void updateAgence() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("nom", this.nom)
                .append("adresse", this.adresse)
                .append("telephone", this.telephone)
                .append("email", this.email));

        collection.updateOne(filter, update);
    }

    public void deleteAgence() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    // Méthodes de recherche et de récupération d'agences
    public static Agence getAgenceById(ObjectId agenceId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Document result = collection.find(eq("_id", agenceId)).first();

        if (result != null) {
            Agence agence = new Agence(result.getString("nom"), result.getString("adresse"),
                    result.getString("telephone"), result.getString("email"));
            agence.setId(result.getObjectId("_id"));
            return agence;
        }

        return null;
    }

    // Méthode pour créer un index secondaire sur le champ "email"
    public static void createEmailIndex() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        IndexOptions indexOptions = new IndexOptions().unique(true);
        collection.createIndex(new Document("email", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getAgencesWithClients() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> agencesCollection = database.getCollection("agence");
        MongoCollection<Document> clientsCollection = database.getCollection("client");

        List<Document> agencesWithClients = new ArrayList<>();

        MongoCursor<Document> agencesCursor = agencesCollection.find().iterator();
        while (agencesCursor.hasNext()) {
            Document agence = agencesCursor.next();
            String agenceId = agence.getString("_id");

            List<Document> clients = clientsCollection.find(new Document("agenceId", agenceId)).into(new ArrayList<>());
            agence.append("clients", clients);

            agencesWithClients.add(agence);
        }

        return agencesWithClients;
    }

    // Groupement
    public static List<Document> getAgencesByVille() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Bson group = Aggregates.group("$ville", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }

    // Tri
    public static List<Document> getAgencesSortedByNom() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        FindIterable<Document> result = collection.find().sort(Sorts.ascending("nom"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void updateNomsInBulk(String oldName, String newName) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("agence");

        Bson filter = eq("nom", oldName);
        Bson update = Updates.set("nom", newName);

        collection.updateMany(filter, update);
    }

    // Méthodes utilitaires
    private static List<Document> toList(MongoIterable<Document> iterable) {
        List<Document> list = new ArrayList<>();
        iterable.into(list);
        return list;
    }
}
