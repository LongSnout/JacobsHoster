package sync;

import service.PrerregistroService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PrerregistroSyncService {

    private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> tarea;

    private PrerregistroSyncService() {}

    public static void iniciar(Runnable onNuevosPrerregistros) {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("[Sync] Ya estaba iniciado.");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "prerregistro-sync");
            t.setDaemon(true);
            return t;
        });

        tarea = scheduler.scheduleAtFixedRate(() -> {
            try {
                int nuevos = PrerregistroService.recibirDesdeNube();
                if (nuevos > 0 && onNuevosPrerregistros != null) {
                    javafx.application.Platform.runLater(onNuevosPrerregistros);
                }
            } catch (Exception e) {
                System.err.println("[Sync] Error en ciclo de sincronización: " + e.getMessage());
            }
        }, 0, config.AppConfig.SYNC_INTERVAL_SECONDS, TimeUnit.SECONDS);

        System.out.println("[Sync] Sincronización iniciada cada "
            + config.AppConfig.SYNC_INTERVAL_SECONDS + "s");
    }

    public static void detener() {
        if (scheduler != null) {
            scheduler.shutdown();
            System.out.println("[Sync] Sincronización detenida.");
        }
    }
}