package neo4j;

import org.junit.jupiter.api.*;
import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgenceTest {
    private static Driver driver;

    @BeforeAll
    public static void setup() {
        String uri = "neo4j+s://91d8e935.databases.neo4j.io";
        String user = "neo4j";
        String password = "K01Vs880wyeLSSe6kJXXS2eX93c1Inos9W7ZV3PAFi0";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @BeforeEach
    public void clearData() {
        try (Session session = driver.session()) {
            session.run("MATCH (a:Agence) DETACH DELETE a");
        }
    }

    @AfterAll
    public static void teardown() {
        driver.close();
    }

    @Test
    @Order(1)
    public void testInsertAgence() throws InterruptedException {
        Agence agence = new Agence("Agence A", "Address A", "123456789", "email@example.com");

        agence.insertAgence(driver);

        Agence retrievedAgence = Agence.getAgenceById(driver, agence.getId());

        assertNotNull(retrievedAgence);
        assertEquals(agence.getId(), retrievedAgence.getId());
        assertEquals(agence.getNom(), retrievedAgence.getNom());
        assertEquals(agence.getAdresse(), retrievedAgence.getAdresse());
        assertEquals(agence.getTelephone(), retrievedAgence.getTelephone());
        assertEquals(agence.getEmail(), retrievedAgence.getEmail());
    }

    @Test
    @Order(2)
    public void testUpdateAgence() {
        Agence agence = new Agence("Agence A (update)", "Address A", "123456789", "email@example.com");
        agence.insertAgence(driver);

        agence.setNom("Agence Updated");
        agence.setAdresse("Updated Address");
        agence.setTelephone("987654321");
        agence.setEmail("updated-email@example.com");

        agence.updateAgence(driver);

        Agence retrievedAgence = Agence.getAgenceById(driver, agence.getId());

        assertNotNull(retrievedAgence);
        assertEquals(agence.getId(), retrievedAgence.getId());
        assertEquals(agence.getNom(), retrievedAgence.getNom());
        assertEquals(agence.getAdresse(), retrievedAgence.getAdresse());
        assertEquals(agence.getTelephone(), retrievedAgence.getTelephone());
        assertEquals(agence.getEmail(), retrievedAgence.getEmail());
    }

    @Test
    @Order(3)
    public void testDeleteAgence() {
        Agence agence = new Agence("Agence A", "Address A", "123456789", "email@example.com");
        agence.insertAgence(driver);

        agence.deleteAgence(driver);

        Agence retrievedAgence = Agence.getAgenceById(driver, agence.getId());

        assertNull(retrievedAgence);
    }

    @Test
    @Order(4)
    public void testGetAgencesSortedByNom() {
        // Create and insert some Agence objects with different names
        Agence agence1 = new Agence("Agence C", "Address A", "123456789", "email1@example.com");
        Agence agence2 = new Agence("Agence A", "Address B", "987654321", "email2@example.com");
        Agence agence3 = new Agence("Agence B", "Address C", "456789123", "email3@example.com");
        agence1.insertAgence(driver);
        agence2.insertAgence(driver);
        agence3.insertAgence(driver);

        // Retrieve the Agences sorted by name
        List<Agence> agencesSortedByNom = Agence.getAgencesSortedByNom(driver);

        // Assert that the list is not null and contains the correct number of Agences
        assertNotNull(agencesSortedByNom);
        assertEquals(3, agencesSortedByNom.size());

        // Assert that the Agences are sorted correctly by name
        assertEquals("Agence A", agencesSortedByNom.get(0).getNom());
        assertEquals("Agence B", agencesSortedByNom.get(1).getNom());
        assertEquals("Agence C", agencesSortedByNom.get(2).getNom());
    }
}
