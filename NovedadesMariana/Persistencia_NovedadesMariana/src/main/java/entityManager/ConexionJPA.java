/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entityManager;
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
        
        emf = Persistence.createEntityManagerFactory("com.itson_Dominio_NovedadesMariana_jar_1.0-SNAPSHOTPU");
    }

    public static ConexionJPA getInstance() {
        if (instancia == null) {
            instancia = new ConexionJPA();
        }
        return instancia;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}