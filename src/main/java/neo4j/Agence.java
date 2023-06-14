package neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class Agence {
    private String id;
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
        employes = new ArrayList<>();
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

    public void insertAgence(Driver driver) {
        try (Session session = driver.session()) {
            String query = "CREATE (a:Agence {nom: $nom, adresse: $adresse, telephone: $telephone, email: $email})";
            session.run(query, Values.parameters(
                    "nom", this.nom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "email", this.email
            ));
        }
    }

    public void updateAgence(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Agence {id: $id}) SET a.nom = $nom, a.adresse = $adresse, a.telephone = $telephone, a.email = $email";
            session.run(query, Values.parameters(
                    "id", this.id,
                    "nom", this.nom,
                    "adresse", this.adresse,
                    "telephone", this.telephone,
                    "email", this.email
            ));
        }
    }

    public void deleteAgence(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Agence {id: $id}) DELETE a";
            session.run(query, Values.parameters(
                    "id", this.id
            ));
        }
    }

    // Méthodes de recherche et de récupération d'agences

    public static Agence getAgenceById(Driver driver, String agenceId) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Agence {id: $id}) RETURN a";
            Record record = session.run(query, Values.parameters(
                    "id", agenceId
            )).single();

            if (record != null) {
                Node node = record.get("a").asNode();
                Agence agence = new Agence(
                        node.get("nom").asString(),
                        node.get("adresse").asString(),
                        node.get("telephone").asString(),
                        node.get("email").asString()
                );
                agence.setId(agenceId);
                return agence;
            }
        }

        return null;
    }

    // Méthodes applicatives de consultation

    // Jointure
//    public static List<Agence> getAgencesWithClients(Driver driver) {
//        try (Session session = driver.session()) {
//            String query = "MATCH (a:Agence)-[:EMPLOIE]->(e:Employe)-[:TRAVAILLE_A]->(c:Client) " +
//                    "RETURN a, COLLECT(DISTINCT {nom: e.nom, adresse: c.adresse, telephone: c.telephone, email: c.email}) AS clients";
//            Result result = session.run(query);
//
//            List<Agence> agencesWithClients = new ArrayList<>();
//
//            while (result.hasNext()) {
//                Record record = result.next();
//                Node agenceNode = record.get("a").asNode();
//                List<Value> clientsValues = record.get("clients").asList();
//
//                Agence agence = new Agence(
//                        agenceNode.get("nom").asString(),
//                        agenceNode.get("adresse").asString(),
//                        agenceNode.get("telephone").asString(),
//                        agenceNode.get("email").asString()
//                );
//                agence.setId(agenceNode.get("id").asString());
//
//                for (Value clientValue : clientsValues) {
//                    Node clientNode = clientValue.asNode();
//                    Employe employe = new Employe (
//                            clientNode.get("nom").asString(),
//                            clientNode.get("adresse").asString(),
//                            clientNode.get("telephone").asString(),
//                            clientNode.get("email").asString()
//                    );
//                    agence.addEmploye(employe);
//                }
//
//                agencesWithClients.add(agence);
//            }
//
//            return agencesWithClients;
//        }
//    }

    // Groupement
    public static List<Agence> getAgencesByVille(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Agence) RETURN a.ville AS ville, COUNT(*) AS count ORDER BY ville";
            Result result = session.run(query);

            List<Agence> agencesByVille = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                String ville = record.get("ville").asString();
                long count = record.get("count").asLong();

                Agence agence = new Agence(null, null, null, null);
                agence.setNom(ville);
                agence.setAdresse("Count: " + count);

                agencesByVille.add(agence);
            }

            return agencesByVille;
        }
    }

    // Tri
    public static List<Agence> getAgencesSortedByNom(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Agence) RETURN a ORDER BY a.nom";
            Result result = session.run(query);

            List<Agence> agencesSortedByNom = new ArrayList<>();

            while (result.hasNext()) {
                Record record = result.next();
                Node node = record.get("a").asNode();

                Agence agence = new Agence(
                        node.get("nom").asString(),
                        node.get("adresse").asString(),
                        node.get("telephone").asString(),
                        node.get("email").asString()
                );
                agence.setId(node.get("id").asString());

                agencesSortedByNom.add(agence);
            }

            return agencesSortedByNom;
        }
    }

    // Méthodes utilitaires
}

