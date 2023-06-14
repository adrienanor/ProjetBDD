package neo4j;

import org.junit.jupiter.api.*;

import org.neo4j.driver.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientTest {
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
        // Clear the test data before each test
        try (Session session = driver.session()) {
            session.run("MATCH (c:Client) DETACH DELETE c");
        }
    }

    @AfterAll
    public static void teardown() {
        // Close the Neo4j driver
        driver.close();
    }

    @Test
    @Order(1)
    public void testInsertClient() {
        // Create an agency
        Agence agence = new Agence("Agency A", "Address A", "123456789", "agencyA@example.com");
        agence.insertAgence(driver);

        // Create a new Client object
        Client client = new Client("John", "Doe", "Address 1", "987654321", "2000-01-01", "john.doe@example.com", agence);

        // Insert the Client into the database
        client.insertClient(driver);

        // Retrieve the Client from the database by ID
        Client retrievedClient = Client.getClientById(driver, client.getId());

        // Assert that the retrieved Client is not null and has the same properties as the original Client
        assertNotNull(retrievedClient);
        assertEquals(client.getId(), retrievedClient.getId());
        assertEquals(client.getNom(), retrievedClient.getNom());
        assertEquals(client.getPrenom(), retrievedClient.getPrenom());
        assertEquals(client.getAdresse(), retrievedClient.getAdresse());
        assertEquals(client.getTelephone(), retrievedClient.getTelephone());
        assertEquals(client.getDateNaissance(), retrievedClient.getDateNaissance());
        assertEquals(client.getEmail(), retrievedClient.getEmail());
        assertEquals(client.getAgence().getId(), retrievedClient.getAgence().getId());
    }

    @Test
    @Order(2)
    public void testUpdateClient() {
        // Create an agency
        Agence agence = new Agence("Agency A", "Address A", "123456789", "agencyA@example.com");
        agence.insertAgence(driver);

        // Create a new Client object
        Client client = new Client("John", "Doe", "Address 1", "987654321", "2000-01-01", "john.doe@example.com", agence);
        client.insertClient(driver);

        // Update the Client's properties
        client.setNom("Updated Name");
        client.setAdresse("Updated Address");
        client.setTelephone("555555555");
        client.setEmail("updated-email@example.com");
        client.updateClient(driver);

        // Retrieve the updated Client from the database
        Client retrievedClient = Client.getClientById(driver, client.getId());

        // Assert that the retrieved Client is not null and has the updated properties
        assertNotNull(retrievedClient);
        assertEquals(client.getId(), retrievedClient.getId());
        assertEquals("Updated Name", retrievedClient.getNom());
        assertEquals("Updated Address", retrievedClient.getAdresse());
        assertEquals("555555555", retrievedClient.getTelephone());
        assertEquals("updated-email@example.com", retrievedClient.getEmail());
        assertEquals(client.getAgence().getId(), retrievedClient.getAgence().getId());
    }

    @Test
    @Order(3)
    public void testDeleteClient() {
        // Create an agency
        Agence agence = new Agence("Agency A", "Address A", "123456789", "agencyA@example.com");
        agence.insertAgence(driver);

        // Create a new Client object
        Client client = new Client("John", "Doe", "Address 1", "987654321", "2000-01-01", "john.doe@example.com", agence);
        client.insertClient(driver);

        // Delete the Client from the database
        client.deleteClient(driver);

        // Retrieve the deleted Client from the database
        Client retrievedClient = Client.getClientById(driver, client.getId());

        // Assert that the retrieved Client is null (not found)
        assertNull(retrievedClient);
    }

    @Test
    @Order(4)
    public void testGetClientsSortedByNom() {
        // Create an agency
        Agence agence = new Agence("Agency A", "Address A", "123456789", "agencyA@example.com");
        agence.insertAgence(driver);

        // Create and insert some Client objects with different names
        Client client1 = new Client("John", "Doe", "Address 1", "987654321", "2000-01-01", "john.doe@example.com", agence);
        Client client2 = new Client("Alice", "Smith", "Address 2", "555555555", "1995-05-05", "alice.smith@example.com", agence);
        Client client3 = new Client("Bob", "Johnson", "Address 3", "111111111", "1980-10-10", "bob.johnson@example.com", agence);
        client1.insertClient(driver);
        client2.insertClient(driver);
        client3.insertClient(driver);

        // Retrieve the Clients sorted by name
        List<Client> clientsSortedByNom = Client.getClientsSortedByNom(driver);

        // Assert that the list is not null and contains the correct number of Clients
        assertNotNull(clientsSortedByNom);
        assertEquals(3, clientsSortedByNom.size());

        // Assert that the Clients are sorted correctly by name
        assertEquals("Alice", clientsSortedByNom.get(0).getNom());
        assertEquals("Bob", clientsSortedByNom.get(1).getNom());
        assertEquals("John", clientsSortedByNom.get(2).getNom());
    }
}
