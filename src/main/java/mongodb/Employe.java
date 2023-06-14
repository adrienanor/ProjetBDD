package mongodb;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class Employe {
    private String id;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private String dateNaissance;
    private String dateEmbauche;
    private double salaire;
    private Agence agence;

    // Constructeur
    public Employe(String nom, String prenom, String adresse, String telephone, String email, String dateNaissance, String dateEmbauche, double salaire, Agence agence) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.agence = agence;
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

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(String dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    // Méthodes CRUD

    public void insertEmploye() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        Document document = new Document();
        document.append("nom", this.nom);
        document.append("prenom", this.prenom);
        document.append("adresse", this.adresse);
        document.append("telephone", this.telephone);
        document.append("email", this.email);
        document.append("dateNaissance", this.dateNaissance);
        document.append("dateEmbauche", this.dateEmbauche);
        document.append("salaire", this.salaire);

        collection.insertOne(document);

        this.setId(document.getObjectId("_id").toString());
    }

    public void updateEmploye() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        Document filter = new Document("_id", this.id);
        Document update = new Document("$set", new Document()
                .append("nom", this.nom)
                .append("prenom", this.prenom)
                .append("adresse", this.adresse)
                .append("telephone", this.telephone)
                .append("email", this.email)
                .append("dateNaissance", this.dateNaissance)
                .append("dateEmbauche", this.dateEmbauche)
                .append("salaire", this.salaire));

        collection.updateOne(filter, update);
    }

    public void deleteEmploye() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        Document filter = new Document("_id", this.id);
        collection.deleteOne(filter);
    }

    // Méthodes de recherche et de récupération d'employés

    public static Employe getEmployeById(String employeId) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        Document filter = new Document("_id", employeId);
        Document result = collection.find(filter).first();

        if (result != null) {
            Employe employe = new Employe(result.getString("nom"), result.getString("prenom"),
                    result.getString("adresse"), result.getString("telephone"), result.getString("email"),
                    result.getString("dateNaissance"), result.getString("dateEmbauche"), result.getDouble("salaire"), Agence.getAgenceById(result.getString("agence")));
            employe.setId(result.getObjectId("_id").toString());
            return employe;
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
        MongoCollection<Document> collection = database.getCollection("employe");

        IndexOptions indexOptions = new IndexOptions().unique(true);
        collection.createIndex(new Document("email", 1), indexOptions);
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static List<Document> getEmployesWithClients() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> employesCollection = database.getCollection("employe");
        MongoCollection<Document> clientsCollection = database.getCollection("client");

        List<Document> employesWithClients = new ArrayList<>();

        MongoCursor<Document> employesCursor = employesCollection.find().iterator();
        while (employesCursor.hasNext()) {
            Document employe = employesCursor.next();
            String employeId = employe.getString("_id");

            Document clientDoc = clientsCollection.find(new Document("employeId", employeId)).first();
            employe.append("client", clientDoc);

            employesWithClients.add(employe);
        }

        return employesWithClients;
    }

    // Groupement
    public static List<Document> getEmployesBySalaireRange(double minSalaire, double maxSalaire) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        Bson filter = Filters.and(
                Filters.gte("salaire", minSalaire),
                Filters.lte("salaire", maxSalaire)
        );

        Bson group = Aggregates.group("$salaire", Accumulators.sum("count", 1));

        AggregateIterable<Document> result = collection.aggregate(List.of(group, Aggregates.sort(Sorts.ascending("_id"))));

        return toList(result);
    }

    // Tri
    public static List<Document> getEmployesSortedByNom() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        FindIterable<Document> result = collection.find().sort(Sorts.ascending("nom"));

        return toList(result);
    }

    // Traitement en masse de documents
    public static void updateSalairesInBulk(double percentageIncrease) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://projetbddadmin:projetbddadmin@projetbdd.qhobbmh.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        MongoClient client = MongoClients.create(settings);
        MongoDatabase database = client.getDatabase("projetBDD");
        MongoCollection<Document> collection = database.getCollection("employe");

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        FindIterable<Document> employes = collection.find();
        for (Document employe : employes) {
            double salaire = employe.getDouble("salaire");
            double newSalaire = salaire + (salaire * percentageIncrease / 100);

            Bson filter = Filters.eq("_id", employe.get("_id"));
            Bson update = Updates.set("salaire", newSalaire);

            bulkOperations.add(new UpdateOneModel<>(filter, update));
        }

        collection.bulkWrite(bulkOperations);
        client.close();
    }

    // Méthodes utilitaires

    private static List<Document> toList(MongoIterable<Document> iterable) {
        List<Document> list = new ArrayList<>();
        iterable.forEach((Block<? super Document>) (Document document) -> list.add(document));
        return list;
    }
}
