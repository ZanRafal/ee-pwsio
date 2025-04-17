import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolParticleSimulation {
    private final int simulationSteps;
    private final int numParticles;
    private final List<Particle> particles = new ArrayList<>();

    public ThreadPoolParticleSimulation(int numParticles, int simulationSteps) {
        this.simulationSteps = simulationSteps;
        this.numParticles = numParticles;
        var random = new Random();

        for (int i = 0; i < numParticles; i++) {
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 1000;
            double vx = random.nextDouble() * 2 - 1;
            double vy = random.nextDouble() * 2 - 1;
            particles.add(new Particle(x, y, vx, vy, i));
        }
    }

    public void runSimulation() {
        long startTime = System.currentTimeMillis();

        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        System.out.println("Utworzono pulę z " + threadCount + " wątkami");

        CountDownLatch latch = new CountDownLatch(numParticles);

        for (Particle particle : particles) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < simulationSteps; i++) {
                        particle.update();

                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Implementacja z pulą wątków zakończona w czasie: " + (endTime - startTime) + " ms");
    }
}


