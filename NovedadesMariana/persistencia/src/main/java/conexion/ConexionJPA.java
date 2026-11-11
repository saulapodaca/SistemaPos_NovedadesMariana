package conexion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author rauln
 */
public class ConexionJPA {
    private static ConexionJPA instancia;
    private final EntityManagerFactory emf;

    private ConexionJPA() {
        // Detecta automáticamente si se está ejecutando desde Maven Surefire (mvn test)
        boolean esTest = System.getProperty("surefire.test.class.path") != null;
        
        String unidadPersistencia = esTest 
            ? "persistenciaPU_Test" 
            : "persistenciaPU";
            
        emf = Persistence.createEntityManagerFactory(unidadPersistencia);
    }

    public static synchronized ConexionJPA getInstance() {
        if (instancia == null) {
            instancia = new ConexionJPA();
        }
        return instancia;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}