import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledThreadPoolParticleSimulation {
    private final int numParticles;
    private final int simulationSteps;
    private final List<Particle> particles = new ArrayList<>();

    public ScheduledThreadPoolParticleSimulation(int numParticles, int simulationSteps) {
        this.numParticles = numParticles;
        this.simulationSteps = simulationSteps;

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
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);
        System.out.println("Utworzono pulę zaplanowanych zadań z " + threadCount + " wątkami");

        CountDownLatch latch = new CountDownLatch(numParticles);

        for (Particle particle : particles) {
            AtomicInteger step = new AtomicInteger(0);

            scheduler.scheduleWithFixedDelay(() -> {
                if (step.get() < simulationSteps) {
                    particle.update();
                    step.incrementAndGet();
                } else {
                    latch.countDown();
                    throw new RuntimeException("Task completed");
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Implementacja z ScheduledExecutorService zakończona w czasie: " + (endTime - startTime) + " ms");
    }
}
