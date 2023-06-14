package neo4j;

import org.junit.jupiter.api.*;
import org.neo4j.driver.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompteTest {
    private static Driver driver;
    private static Client testClient;
    private static Agence testAgence;

    @BeforeAll
    public static void setup() {
        String uri = "neo4j+s://91d8e935.databases.neo4j.io";
        String user = "neo4j";
        String password = "K01Vs880wyeLSSe6kJXXS2eX93c1Inos9W7ZV3PAFi0";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

        testAgence = new Agence("Test Agence", "123 Test St", "555-1234", "agence@example.com");
        testAgence.insertAgence(driver);

        testClient = new Client("John", "Doe", "123 Main St", "555-1234", "2000-01-01", "john.doe@example.com", testAgence);
        testClient.insertClient(driver);
    }

    @BeforeEach
    public void clearData() {
        try (Session session = driver.session()) {
            session.run("MATCH (c:Compte) DETACH DELETE c");
        }
    }

    @AfterAll
    public static void teardown() {
        testClient.deleteClient(driver);

        testAgence.deleteAgence(driver);

        driver.close();
    }

    @Test
    @Order(1)
    public void testInsertCompte() {
        Compte compte = new Compte(testClient, "Savings", 1000.0, "ABC123", "DEF456");

        compte.insertCompte(driver);

        assertNotNull(compte.getId());
    }

    @Test
    @Order(2)
    public void testUpdateCompte() {
        Compte compte = new Compte(testClient, "Savings", 1000.0, "ABC123", "DEF456");
        compte.insertCompte(driver);

        compte.setTypeCompte("Checking");
        compte.setSolde(2000.0);
        compte.setIBAN("XYZ789");
        compte.setBIC("GHI012");

        compte.updateCompte(driver);

        Compte updatedCompte = Compte.getCompteById(driver, compte.getId());

        assertNotNull(updatedCompte);
        assertEquals(compte.getId(), updatedCompte.getId());
        assertEquals(compte.getClient().getId(), updatedCompte.getClient().getId());
        assertEquals(compte.getTypeCompte(), updatedCompte.getTypeCompte());
        assertEquals(compte.getSolde(), updatedCompte.getSolde());
        assertEquals(compte.getIBAN(), updatedCompte.getIBAN());
        assertEquals(compte.getBIC(), updatedCompte.getBIC());
    }

    @Test
    @Order(3)
    public void testDeleteCompte() {
        Compte compte = new Compte(testClient, "Savings", 1000.0, "ABC123", "DEF456");
        compte.insertCompte(driver);

        compte.deleteCompte(driver);

        Compte deletedCompte = Compte.getCompteById(driver, compte.getId());

        assertNull(deletedCompte);
    }

    @Test
    @Order(4)
    public void testGetCompteById() {
        Compte compte = new Compte(testClient, "Savings", 1000.0, "ABC123", "DEF456");
        compte.insertCompte(driver);

        Compte retrievedCompte = Compte.getCompteById(driver, compte.getId());

        assertNotNull(retrievedCompte);
        assertEquals(compte.getId(), retrievedCompte.getId());
    }
}
