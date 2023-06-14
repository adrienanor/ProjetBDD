package neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;

import static org.neo4j.driver.Values.parameters;

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

    public void insertCompte(Driver driver) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "CREATE (c:Compte {clientId: $clientId, typeCompte: $typeCompte, solde: $solde, IBAN: $IBAN, BIC: $BIC})",
                    parameters(
                            "clientId", this.client.getId(),
                            "typeCompte", this.typeCompte,
                            "solde", this.solde,
                            "IBAN", this.IBAN,
                            "BIC", this.BIC
                    )
            ));
        }
    }

    public void updateCompte(Driver driver) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (c:Compte {id: $id}) " +
                            "SET c.clientId = $clientId, c.typeCompte = $typeCompte, c.IBAN = $IBAN, c.BIC = $BIC, c.solde = $solde",
                    parameters(
                            "id", this.id,
                            "clientId", this.client.getId(),
                            "typeCompte", this.typeCompte,
                            "IBAN", this.IBAN,
                            "BIC", this.BIC,
                            "solde", this.solde
                    )
            ));
        }
    }

    public void deleteCompte(Driver driver) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (c:Compte {id: $id}) DELETE c",
                    parameters("id", this.id)
            ));
        }
    }

    // Méthodes de recherche et de récupération de comptes

    public static Compte getCompteById(Driver driver, String compteId) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (c:Compte {id: $id}) RETURN c",
                        parameters("id", compteId)
                );
                if (result.hasNext()) {
                    Record record = result.next();
                    Node compteNode = record.get("c").asNode();
                    Compte compte = new Compte(Client.getClientById(driver, compteNode.get("clientId").asString()),
                            compteNode.get("typeCompte").asString(),
                            compteNode.get("solde").asDouble(),
                            compteNode.get("IBAN").asString(),
                            compteNode.get("BIC").asString());
                    compte.setId(compteNode.get("id").asString());
                    return compte;
                }
                return null;
            });
        }
    }

    // Méthode pour créer un index secondaire sur le champ "clientId"
    public static void createClientIdIndex(Driver driver) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("CREATE INDEX ON :Compte(clientId)"));
        }
    }

    // Méthodes applicatives de consultation

    // Jointure
    public static Result getComptesWithClients(Driver driver) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> tx.run(
                    "MATCH (c:Compte)-[:HAS_CLIENT]->(cl:Client) RETURN c, cl"
            ));
        }
    }

    // Groupement
    public static Result getComptesByType(Driver driver) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> tx.run(
                    "MATCH (c:Compte) RETURN c.typeCompte AS typeCompte, count(*) AS count ORDER BY typeCompte ASC"
            ));
        }
    }

    // Tri
    public static Result getComptesSortedBySolde(Driver driver) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> tx.run(
                    "MATCH (c:Compte) RETURN c ORDER BY c.solde DESC"
            ));
        }
    }

    // Traitement en masse de documents
    public static void updateSoldeInBulk(Driver driver, double amount) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (c:Compte) SET c.solde = c.solde + $amount",
                    parameters("amount", amount)
            ));
        }
    }
}
