package mongodb;

import org.junit.jupiter.api.*;
import java.util.UUID;

public class AgenceTest {

    private Agence agence;

    @BeforeEach
    public void setup() {
        String uniqueId = UUID.randomUUID().toString();

        agence = new Agence("Agence " + uniqueId, "Agency Address " + uniqueId,
                "1234567890", "agency" + uniqueId + "@example.com");

        agence.insertAgence();
    }

    @AfterEach
    public void cleanup() {
        agence.deleteAgence();
    }

    @Test
    public void testInsertAgence() {
        Agence retrievedAgence = Agence.getAgenceById(agence.getId());

        Assertions.assertNotNull(retrievedAgence);

        Assertions.assertEquals(agence.getNom(), retrievedAgence.getNom());
        Assertions.assertEquals(agence.getAdresse(), retrievedAgence.getAdresse());
        Assertions.assertEquals(agence.getTelephone(), retrievedAgence.getTelephone());
        Assertions.assertEquals(agence.getEmail(), retrievedAgence.getEmail());
    }
}

