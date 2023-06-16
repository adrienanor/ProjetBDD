package neo4j;

import org.junit.jupiter.api.*;
import org.neo4j.driver.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeTest {
    private static Driver driver;
    private static Agence testAgence;

    @BeforeAll
    public static void setup() {
        String uri = "neo4j+s://91d8e935.databases.neo4j.io";
        String user = "neo4j";
        String password = "K01Vs880wyeLSSe6kJXXS2eX93c1Inos9W7ZV3PAFi0";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

        testAgence = new Agence("Test Agence", "123 Test St", "555-1234", "agence@example.com");
        testAgence.insertAgence(driver);
    }

    @BeforeEach
    public void clearData() {
        try (Session session = driver.session()) {
            session.run("MATCH (e:Employe) DETACH DELETE e");
        }
    }

    @AfterAll
    public static void teardown() {
        testAgence.deleteAgence(driver);

        driver.close();
    }

    @Test
    @Order(1)
    public void testInsertEmploye() {
        Employe employe = new Employe("John", "Doe", "123 Main St", "555-1234", "john.doe@example.com", "1980-01-01", "2022-01-01", 5000.0, testAgence);

        employe.insertEmploye(driver);

        assertNotNull(Employe.getEmployeById(driver, employe.getId()));
    }

    @Test
    @Order(2)
    public void testUpdateEmploye() {
        Employe employe = new Employe("John", "Doe", "123 Main St", "555-1234", "john.doe@example.com", "1980-01-01", "2022-01-01", 5000.0, testAgence);
        employe.insertEmploye(driver);

        employe.setAdresse("456 New St");
        employe.setTelephone("555-5678");
        employe.setEmail("john.doe@example.org");

        employe.updateEmploye(driver);

        Employe updatedEmploye = Employe.getEmployeById(driver, employe.getId());

        assertNotNull(updatedEmploye);
        assertEquals(employe.getId(), updatedEmploye.getId());
        assertEquals(employe.getNom(), updatedEmploye.getNom());
        assertEquals(employe.getPrenom(), updatedEmploye.getPrenom());
        assertEquals(employe.getAdresse(), updatedEmploye.getAdresse());
        assertEquals(employe.getTelephone(), updatedEmploye.getTelephone());
        assertEquals(employe.getEmail(), updatedEmploye.getEmail());
        assertEquals(employe.getDateNaissance(), updatedEmploye.getDateNaissance());
        assertEquals(employe.getDateEmbauche(), updatedEmploye.getDateEmbauche());
        assertEquals(employe.getSalaire(), updatedEmploye.getSalaire());
        assertEquals(employe.getAgence().getId(), updatedEmploye.getAgence().getId());
    }

    @Test
    @Order(3)
    public void testDeleteEmploye() {
        Employe employe = new Employe("John", "Doe", "123 Main St", "555-1234", "john.doe@example.com", "1980-01-01", "2022-01-01", 5000.0, testAgence);
        employe.insertEmploye(driver);

        employe.deleteEmploye(driver);

        Employe deletedEmploye = Employe.getEmployeById(driver, employe.getId());

        assertNull(deletedEmploye);
    }

    @Test
    @Order(4)
    public void testGetEmployeById() {
        Employe employe = new Employe("John", "Doe", "123 Main St", "555-1234", "john.doe@example.com", "1980-01-01", "2022-01-01", 5000.0, testAgence);
        employe.insertEmploye(driver);

        Employe retrievedEmploye = Employe.getEmployeById(driver, employe.getId());

        assertNotNull(retrievedEmploye);
        assertEquals(employe.getId(), retrievedEmploye.getId());
    }
}
