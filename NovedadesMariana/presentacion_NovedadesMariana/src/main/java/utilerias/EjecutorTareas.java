package utilerias;

import java.awt.Component;
import java.awt.Cursor;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/** Ejecuta tareas fuera del EDT y entrega sus resultados en el hilo de Swing. */
public final class EjecutorTareas {
    private boolean ocupado;

    public boolean estaOcupado() { return ocupado; }

    public <T> void ejecutar(Component componente, Callable<T> tarea,
            Consumer<T> completado, Consumer<Throwable> error) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Las tareas de la interfaz deben iniciarse desde el EDT.");
        }
        if (ocupado) return;
        ocupado = true;
        Cursor anterior = componente.getCursor();
        componente.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception { return tarea.call(); }

            @Override
            protected void done() {
                ocupado = false;
                componente.setCursor(anterior);
                try {
                    completado.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    error.accept(ex);
                } catch (ExecutionException ex) {
                    error.accept(ex.getCause());
                } catch (RuntimeException ex) {
                    error.accept(ex);
                }
            }
        }.execute();
    }
}