package neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.util.Pair;

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

    public void insertEmploye(Driver driver) {
        try (Session session = driver.session()) {
            String query = "CREATE (e:Employe {nom: $nom, prenom: $prenom, adresse: $adresse, telephone: $telephone, email: $email, dateNaissance: $dateNaissance, dateEmbauche: $dateEmbauche, salaire: $salaire}) RETURN e";
            Value parameters = Values.parameters(
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "email", this.email,
                    "dateNaissance", this.dateNaissance,
                    "dateEmbauche", this.dateEmbauche,
                    "salaire", this.salaire
            );
            Result result = session.run(query, parameters);
            this.id = result.single().get("e").asNode().get("id").asString();
        }
    }

    public void updateEmploye(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe) WHERE e.id = $id SET e.nom = $nom, e.prenom = $prenom, e.adresse = $adresse, e.telephone = $telephone, e.email = $email, e.dateNaissance = $dateNaissance, e.dateEmbauche = $dateEmbauche, e.salaire = $salaire";
            Value parameters = Values.parameters(
                    "id", this.id,
                    "nom", this.nom,
                    "prenom", this.prenom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "email", this.email,
                    "dateNaissance", this.dateNaissance,
                    "dateEmbauche", this.dateEmbauche,
                    "salaire", this.salaire
            );
            session.run(query, parameters);
        }
    }

    public void deleteEmploye(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe) WHERE e.id = $id DELETE e";
            Value parameters = Values.parameters("id", this.id);
            session.run(query, parameters);
        }
    }

    // Méthodes de recherche et de récupération d'employés

    public static Employe getEmployeById(Driver driver, String employeId) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe) WHERE e.id = $id RETURN e";
            Value parameters = Values.parameters("id", employeId);
            Result result = session.run(query, parameters);

            if (result.hasNext()) {
                Node employeNode = result.single().get("e").asNode();
                Employe employe = new Employe(
                        employeNode.get("nom").asString(),
                        employeNode.get("prenom").asString(),
                        employeNode.get("adresse").asString(),
                        employeNode.get("telephone").asString(),
                        employeNode.get("email").asString(),
                        employeNode.get("dateNaissance").asString(),
                        employeNode.get("dateEmbauche").asString(),
                        employeNode.get("salaire").asDouble(),
                        Agence.getAgenceById(driver, employeNode.get("agenceId").asString())
                );
                employe.setId(employeNode.get("id").asString());
                return employe;
            }

            return null;
        }
    }

    // Méthode pour créer une relation "TRAVAILLE_DANS" entre un employé et une agence
    public void travailleDansAgence(Driver driver, Agence agence) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe), (a:Agence) WHERE e.id = $employeId AND a.id = $agenceId CREATE (e)-[:TRAVAILLE_DANS]->(a)";
            Value parameters = Values.parameters(
                    "employeId", this.id,
                    "agenceId", agence.getId()
            );
            session.run(query, parameters);
            this.agence = agence;
        }
    }

    // Méthodes applicatives de consultation

    // Jointure
//    public static List<Pair<Employe, Agence>> getEmployesWithAgence(Driver driver) {
//        try (Session session = driver.session()) {
//            String query = "MATCH (e:Employe)-[:TRAVAILLE_DANS]->(a:Agence) RETURN e, a";
//            Result result = session.run(query);
//
//            List<Pair<Employe, Agence>> employesWithAgences = new ArrayList<>();
//            while (result.hasNext()) {
//                Record record = result.next();
//                Node employeNode = record.get("e").asNode();
//                Node agenceNode = record.get("a").asNode();
//
//                Employe employe = new Employe(
//                        employeNode.get("nom").asString(),
//                        employeNode.get("prenom").asString(),
//                        employeNode.get("adresse").asString(),
//                        employeNode.get("telephone").asString(),
//                        employeNode.get("email").asString(),
//                        employeNode.get("dateNaissance").asString(),
//                        employeNode.get("dateEmbauche").asString(),
//                        employeNode.get("salaire").asDouble()
//                );
//                employe.setId(employeNode.get("id").asString());
//
//                Agence agence = new Agence(
//                        agenceNode.get("nom").asString(),
//                        agenceNode.get("adresse").asString(),
//                        agenceNode.get("telephone").asString(),
//                        agenceNode.get("email").asString()
//                );
//                agence.setId(agenceNode.get("id").asString());
//
//                employe.setAgence(agence);
//                employesWithAgences.add(new Pair<>(employe, agence));
//            }
//
//            return employesWithAgences;
//        }
//    }
//
//    // Groupement
//    public static List<Pair<Double, Integer>> getEmployesBySalaireRange(Driver driver, double minSalaire, double maxSalaire) {
//        try (Session session = driver.session()) {
//            String query = "MATCH (e:Employe) WHERE e.salaire >= $minSalaire AND e.salaire <= $maxSalaire RETURN e.salaire AS salaire, count(*) AS count";
//            Value parameters = Values.parameters(
//                    "minSalaire", minSalaire,
//                    "maxSalaire", maxSalaire
//            );
//            Result result = session.run(query, parameters);
//
//            List<Pair<Double, Integer>> employesBySalaireRange = new ArrayList<>();
//            while (result.hasNext()) {
//                Record record = result.next();
//                double salaire = record.get("salaire").asDouble();
//                int count = record.get("count").asInt();
//                employesBySalaireRange.add(new Pair<>(salaire, count));
//            }
//
//            return employesBySalaireRange;
//        }
//    }

    // Tri
    public static List<Employe> getEmployesSortedByNom(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe) RETURN e ORDER BY e.nom";
            Result result = session.run(query);

            List<Employe> employesSortedByNom = new ArrayList<>();
            while (result.hasNext()) {
                Node employeNode = result.next().get("e").asNode();
                Employe employe = new Employe(
                        employeNode.get("nom").asString(),
                        employeNode.get("prenom").asString(),
                        employeNode.get("adresse").asString(),
                        employeNode.get("telephone").asString(),
                        employeNode.get("email").asString(),
                        employeNode.get("dateNaissance").asString(),
                        employeNode.get("dateEmbauche").asString(),
                        employeNode.get("salaire").asDouble(),
                        Agence.getAgenceById(driver, employeNode.get("agenceId").asString())
                );
                employe.setId(employeNode.get("id").asString());
                employesSortedByNom.add(employe);
            }

            return employesSortedByNom;
        }
    }

    // Recherche avec filtres
    public static List<Employe> searchEmployes(Driver driver, String nom, String adresse, String telephone, String email) {
        try (Session session = driver.session()) {
            String query = "MATCH (e:Employe) WHERE e.nom CONTAINS $nom AND e.adresse CONTAINS $adresse AND e.telephone CONTAINS $telephone AND e.email CONTAINS $email RETURN e";
            Value parameters = Values.parameters(
                    "nom", nom,
                    "adresse", adresse,
                    "telephone", telephone,
                    "email", email
            );
            Result result = session.run(query, parameters);

            List<Employe> employes = new ArrayList<>();
            while (result.hasNext()) {
                Node employeNode = result.next().get("e").asNode();
                Employe employe = new Employe(
                        employeNode.get("nom").asString(),
                        employeNode.get("prenom").asString(),
                        employeNode.get("adresse").asString(),
                        employeNode.get("telephone").asString(),
                        employeNode.get("email").asString(),
                        employeNode.get("dateNaissance").asString(),
                        employeNode.get("dateEmbauche").asString(),
                        employeNode.get("salaire").asDouble(),
                        Agence.getAgenceById(driver, employeNode.get("agenceId").asString())
                );
                employe.setId(employeNode.get("id").asString());
                employes.add(employe);
            }

            return employes;
        }
    }
}
