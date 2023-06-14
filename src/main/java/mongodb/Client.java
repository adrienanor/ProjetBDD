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
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class Client {
    private ObjectId id;
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
        this.comptes = new ArrayList<Compte>();
        this.agence = agence;
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

    public String gettelephone() {
        return telephone;
    }

    public void settelephone(String telephone) {
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
    public void insertClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Document document = new Document();
        document.append("nom", this.nom);
        document.append("prenom", this.prenom);
        document.append("adresse", this.adresse);
        document.append("telephone", this.telephone);
        document.append("dateNaissance", this.dateNaissance);
        document.append("email", this.email);
        document.append("agence", this.agence.getId());

        collection.insertOne(document);

        this.setId(document.getObjectId("_id"));
    }

    public void updateClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("nom", this.nom)
                .append("prenom", this.prenom)
                .append("adresse", this.adresse)
                .append("telephone", this.telephone)
                .append("dateNaissance", this.dateNaissance)
                .append("agence", this.agence.getId())
                .append("email", this.email));

        collection.updateOne(filter, update);
    }

    public void deleteClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    public static Client getClientById(ObjectId clientId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Document result = collection.find(eq("_id", clientId)).first();

        if (result != null) {
            Client clientObject = new Client(result.getString("nom"), result.getString("prenom"),
                    result.getString("adresse"), result.getString("telephone"),
                    result.getString("dateNaissance"), result.getString("email"), Agence.getAgenceById(result.getObjectId("agence")));
            clientObject.setId(result.getObjectId("_id"));
            return clientObject;
        }

        return null;
    }

    public static Client getClientByEmail(String email) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Document filter = new Document("email", email);
        Document result = collection.find(filter).first();

        if (result != null) {
            Client clientObject = new Client(result.getString("nom"), result.getString("prenom"),
                    result.getString("adresse"), result.getString("telephone"),
                    result.getString("dateNaissance"), result.getString("email"), Agence.getAgenceById(result.getObjectId("agenceId")));
            clientObject.setId(result.getObjectId("_id"));
            return clientObject;
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
        MongoCollection<Document> collection = database.getCollection("client");

        IndexOptions indexOptions = new IndexOptions().unique(true);
        collection.createIndex(new Document("email", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getClientsWithComptes() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> clientsCollection = database.getCollection("client");
        MongoCollection<Document> comptesCollection = database.getCollection("comte");

        List<Document> clientsWithComptes = new ArrayList<>();

        MongoCursor<Document> clientsCursor = clientsCollection.find().iterator();
        while (clientsCursor.hasNext()) {
            Document clientDoc = clientsCursor.next();
            String clientId = clientDoc.getString("_id");

            FindIterable<Document> comptes = comptesCollection.find(new Document("clientId", clientId));
            clientDoc.append("comptes", toList(comptes));

            clientsWithComptes.add(clientDoc);
        }

        return clientsWithComptes;
    }

    // Groupement
    public static List<Document> getClientsByAgeGroup() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        Bson group = Aggregates.group("$ageGroup", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }

    // Tri
    public static List<Document> getClientsSortedByNom() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

        FindIterable<Document> result = collection.find().sort(Sorts.ascending("nom"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void updateEmailInBulk(String newEmail) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("client");

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
