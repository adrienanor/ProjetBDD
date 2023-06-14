package mongodb;

import org.bson.Document;
import org.junit.jupiter.api.*;
import java.util.UUID;

public class ClientTest {
    private Client client;
    private Agence agence;

    @BeforeEach
    public void setup() {
        agence = new Agence("Agency Name", "Agency Address", "1234567890", "agency@example.com");
        agence.insertAgence();

        String uniqueId = UUID.randomUUID().toString();

        client = new Client("John " + uniqueId, "Doe", "Client Address", "9876543210",
                "2000-01-01", "client" + uniqueId + "@example.com", agence);

        client.insertClient();
    }

    @AfterEach
    public void cleanup() {
        client.deleteClient();
        agence.deleteAgence();
    }

    @Test
    public void testInsertClient() {
        Client retrievedClient = Client.getClientById(client.getId());

        Assertions.assertNotNull(retrievedClient);

        Assertions.assertEquals(client.getNom(), retrievedClient.getNom());
        Assertions.assertEquals(client.getPrenom(), retrievedClient.getPrenom());
        Assertions.assertEquals(client.getAdresse(), retrievedClient.getAdresse());
        Assertions.assertEquals(client.gettelephone(), retrievedClient.gettelephone());
        Assertions.assertEquals(client.getDateNaissance(), retrievedClient.getDateNaissance());
        Assertions.assertEquals(client.getEmail(), retrievedClient.getEmail());
        Assertions.assertEquals(client.getAgence().getId(), retrievedClient.getAgence().getId());
    }
}
