import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ParticleSimulation {
    private final int simulationSteps;
    private final List<Particle> particles = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    public ParticleSimulation(int numParticles, int simulationSteps) {
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
        var startTime = System.currentTimeMillis();

       for (var particle :  particles) {
           var thread = new Thread(new ParticleRunnable(particle, simulationSteps));
           threads.add(thread);
           thread.start();
       }

        for (var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
        }

        var endTime = System.currentTimeMillis();
        System.out.println("Implementacja klasycznego podejścia zakończona w czasie: " + (endTime - startTime) + " ms");
    }

    private record ParticleRunnable(Particle particle, int steps) implements Runnable {

        @Override
            public void run() {
                for (int i = 0; i < steps; i++) {
                    particle.update();

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

            }
        }
}
