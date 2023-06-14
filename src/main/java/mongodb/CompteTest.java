package mongodb;

import org.bson.Document;
import org.junit.jupiter.api.*;
import java.util.UUID;

import org.junit.jupiter.api.*;

public class CompteTest {
    private Compte compte;
    private Agence agence;
    private Client client;

    @BeforeEach
    public void setup() {
        agence = new Agence("Agence Name", "Agence Address", "1234567890", "agence@example.com");
        agence.insertAgence();

        client = new Client("Client Name", "Client Last Name", "Client Address", "9876543210",
                "1990-01-01", "client@example.com", agence);
        client.insertClient();
    }

    @AfterEach
    public void cleanup() {
        client.deleteClient();
        agence.deleteAgence();
        compte.deleteCompte();
    }

    @Test
    public void testInsertCompte() {
        compte = new Compte(client, "Type", 1000.0, "IBAN", "BIC");
        compte.insertCompte();

        Compte retrievedCompte = Compte.getCompteById(compte.getId());

        Assertions.assertNotNull(retrievedCompte);

        Assertions.assertEquals(compte.getTypeCompte(), retrievedCompte.getTypeCompte());
        Assertions.assertEquals(compte.getSolde(), retrievedCompte.getSolde());
        Assertions.assertEquals(compte.getIBAN(), retrievedCompte.getIBAN());
        Assertions.assertEquals(compte.getBIC(), retrievedCompte.getBIC());
        Assertions.assertEquals(compte.getClient().getId(), retrievedCompte.getClient().getId());
        Assertions.assertEquals(compte.getClient().getAgence().getId(), retrievedCompte.getClient().getAgence().getId());
    }
}
