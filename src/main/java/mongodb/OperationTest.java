package mongodb;

import org.junit.jupiter.api.*;
import java.util.Date;

public class OperationTest {
    private Operation operation;
    private Client client;
    private Compte compte;

    @BeforeEach
    public void setup() {
        Agence agence = new Agence("Agence Name", "Agence Address", "1234567890", "agence@example.com");
        agence.insertAgence();

        client = new Client("Client Name", "Client Last Name", "Client Address", "9876543210",
                "1990-01-01", "client@example.com", agence);
        client.insertClient();

        compte = new Compte(client, "Type", 1000.0, "IBAN", "BIC");
        compte.insertCompte();
    }

    @AfterEach
    public void cleanup() {
        compte.deleteCompte();
        client.deleteClient();
        operation.deleteOperation();
    }

    @Test
    public void testInsertOperation() {
        Date currentDate = new Date();

        operation = new Operation(client, compte, currentDate, 500.0, "Type");
        operation.insertOperation();

        Operation retrievedOperation = Operation.getOperationById(operation.getId());

        Assertions.assertNotNull(retrievedOperation);

        Assertions.assertEquals(operation.getClient().getId(), retrievedOperation.getClient().getId());
        Assertions.assertEquals(operation.getCompte().getId(), retrievedOperation.getCompte().getId());
        Assertions.assertEquals(operation.getDate(), retrievedOperation.getDate());
        Assertions.assertEquals(operation.getMontant(), retrievedOperation.getMontant());
        Assertions.assertEquals(operation.getTypeOperation(), retrievedOperation.getTypeOperation());
    }
}