package mongodb;

import org.junit.jupiter.api.*;

public class EmployeTest {
    private Employe employe;
    private Agence agence;

    @BeforeEach
    public void setup() {
        agence = new Agence("Agence Name", "Agence Address", "1234567890", "agence@example.com");
        agence.insertAgence();
    }

    @AfterEach
    public void cleanup() {
        agence.deleteAgence();
        employe.deleteEmploye();
    }

    @Test
    public void testInsertEmploye() {
        employe = new Employe("John", "Doe", "Employee Address", "1234567890", "john.doe@example.com",
                "1990-01-01", "2020-01-01", 5000.0, agence);
        employe.insertEmploye();

        Employe retrievedEmploye = Employe.getEmployeById(employe.getId());

        Assertions.assertNotNull(retrievedEmploye);

        Assertions.assertEquals(employe.getNom(), retrievedEmploye.getNom());
        Assertions.assertEquals(employe.getPrenom(), retrievedEmploye.getPrenom());
        Assertions.assertEquals(employe.getAdresse(), retrievedEmploye.getAdresse());
        Assertions.assertEquals(employe.getTelephone(), retrievedEmploye.getTelephone());
        Assertions.assertEquals(employe.getEmail(), retrievedEmploye.getEmail());
        Assertions.assertEquals(employe.getDateNaissance(), retrievedEmploye.getDateNaissance());
        Assertions.assertEquals(employe.getDateEmbauche(), retrievedEmploye.getDateEmbauche());
        Assertions.assertEquals(employe.getSalaire(), retrievedEmploye.getSalaire());
        Assertions.assertEquals(employe.getAgence().getId(), retrievedEmploye.getAgence().getId());
    }
}
