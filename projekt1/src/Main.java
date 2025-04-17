public class Main {
    private static final int NUM_PARTICLES = 2000;
    private static final int SIMULATION_STEPS = 100;

    public static void main(String[] args) {
        System.out.println("Rozpoczynam porównanie metod wielowątkowych...");
        System.out.println("Liczba dostępnych procesorów: " + Runtime.getRuntime().availableProcessors());

        var classicMonitor = new PerformanceMonitor();
        classicMonitor.start();
        var classicSim = new ParticleSimulation(NUM_PARTICLES, SIMULATION_STEPS);
        classicSim.runSimulation();
        PerformanceMonitor.PerformanceResult classicResult = classicMonitor.end();
        System.out.println("\nWyniki dla klasycznego podejścia (Thread/Runnable):");
        System.out.println(classicResult);

        waitForOneSecond();

        var poolMonitor = new PerformanceMonitor();
        poolMonitor.start();
        var poolSim = new ThreadPoolParticleSimulation(NUM_PARTICLES, SIMULATION_STEPS);
        poolSim.runSimulation();
        PerformanceMonitor.PerformanceResult poolResult = poolMonitor.end();
        System.out.println("\nWyniki dla puli wątków (ExecutorService):");
        System.out.println(poolResult);

        waitForOneSecond();

        var scheduledMonitor = new PerformanceMonitor();
        scheduledMonitor.start();
        var scheduledSim = new ScheduledThreadPoolParticleSimulation(NUM_PARTICLES, SIMULATION_STEPS);
        scheduledSim.runSimulation();
        PerformanceMonitor.PerformanceResult scheduledResult = scheduledMonitor.end();
        System.out.println("\nWyniki dla ScheduledExecutorService:");
        System.out.println(scheduledResult);




        System.out.println("\n--------------------------------------------");
        System.out.println("PODSUMOWANIE PORÓWNANIA:");
        System.out.println("--------------------------------------------");
        System.out.println("1. Klasyczne podejście (Thread/Runnable):");
        System.out.println("   - CPU Time: " + classicResult.cpuTimeMs + " ms");
        System.out.println("   - Zmiana liczby wątków: " + (classicResult.finalThreadCount - classicResult.initialThreadCount));
        System.out.println("   - Zużycie pamięci: " + String.format("%.2f", classicResult.memoryUsageMB) + " MB");

        System.out.println("2. Pula wątków (ExecutorService):");
        System.out.println("   - CPU Time: " + poolResult.cpuTimeMs + " ms");
        System.out.println("   - Zmiana liczby wątków: " + (poolResult.finalThreadCount - poolResult.initialThreadCount));
        System.out.println("   - Zużycie pamięci: " + String.format("%.2f", poolResult.memoryUsageMB) + " MB");

        System.out.println("3. ScheduledExecutorService:");
        System.out.println("   - CPU Time: " + scheduledResult.cpuTimeMs + " ms");
        System.out.println("   - Zmiana liczby wątków: " + (scheduledResult.finalThreadCount - scheduledResult.initialThreadCount));
        System.out.println("   - Zużycie pamięci: " + String.format("%.2f", scheduledResult.memoryUsageMB) + " MB");

        System.out.println("\nANALIZA PORÓWNAWCZA:");
        System.out.println("- ExecutorService vs klasyczne podejście: " +
                String.format("%.2f", classicResult.cpuTimeMs / poolResult.cpuTimeMs) + "x szybciej");
        System.out.println("- ScheduledExecutorService vs klasyczne podejście: " +
                String.format("%.2f", classicResult.cpuTimeMs / scheduledResult.cpuTimeMs) + "x szybciej");
        System.out.println("- Oszczędność wątków przy użyciu puli: " +
                (classicResult.finalThreadCount - poolResult.finalThreadCount) + " wątków");

        System.out.println("\nPorównanie zakończone.");
    }

    private static void waitForOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}