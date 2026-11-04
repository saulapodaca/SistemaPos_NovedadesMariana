package interfaces.persistencia;

import java.util.ServiceLoader;

/** Resuelve la fábrica proporcionada por el módulo de persistencia en ejecución. */
public final class DAOProvider {
    private static final IDAOFactory FACTORY = ServiceLoader
            .load(IDAOFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    "No se encontró un proveedor de contratos DAO en el classpath."));

    private DAOProvider() {
    }

    public static IDAOFactory getFactory() {
        return FACTORY;
    }
}
