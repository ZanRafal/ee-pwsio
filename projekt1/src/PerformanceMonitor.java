import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

public class PerformanceMonitor {
    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private long startCpuTime;
    private long startUserTime;
    private long startSystemTime;
    private int initialThreadCount;
    private final Runtime runtime = Runtime.getRuntime();
    private long initialMemory;
    private final List<ThreadState> threadStates = new ArrayList<>();

    static {
        threadMXBean.setThreadCpuTimeEnabled(true);
    }

    public void start() {
        startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        startUserTime = threadMXBean.getCurrentThreadUserTime();
        startSystemTime = threadMXBean.getCurrentThreadCpuTime() - threadMXBean.getCurrentThreadUserTime();
        initialThreadCount = Thread.activeCount();
        initialMemory = runtime.totalMemory() - runtime.freeMemory();

        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread thread : threads) {
            if (thread != null) {
                threadStates.add(new ThreadState(thread.getId()));
            }
        }
    }

    public PerformanceResult end() {
        long cpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
        long userTime = threadMXBean.getCurrentThreadUserTime() - startUserTime;
        long systemTime = (threadMXBean.getCurrentThreadCpuTime() - threadMXBean.getCurrentThreadUserTime()) - startSystemTime;
        int finalThreadCount = Thread.activeCount();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();

        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        List<ThreadState> finalThreadStates = new ArrayList<>();
        for (Thread thread : threads) {
            if (thread != null) {
                finalThreadStates.add(new ThreadState(thread.getId()));
            }
        }

        return new PerformanceResult(
                cpuTime / 1_000_000.0, // w ms
                userTime / 1_000_000.0,
                systemTime / 1_000_000.0,
                initialThreadCount,
                finalThreadCount,
                (finalMemory - initialMemory) / (1024.0 * 1024.0), // w MB
                threadStates,
                finalThreadStates
        );
    }

    public static class PerformanceResult {
        final double cpuTimeMs;
        private final double userTimeMs;
        private final double systemTimeMs;
        final int initialThreadCount;
        final int finalThreadCount;
        final double memoryUsageMB;
        private final List<ThreadState> initialThreadStates;
        private final List<ThreadState> finalThreadStates;

        public PerformanceResult(double cpuTimeMs, double userTimeMs, double systemTimeMs,
                                 int initialThreadCount, int finalThreadCount, double memoryUsageMB,
                                 List<ThreadState> initialThreadStates, List<ThreadState> finalThreadStates) {
            this.cpuTimeMs = cpuTimeMs;
            this.userTimeMs = userTimeMs;
            this.systemTimeMs = systemTimeMs;
            this.initialThreadCount = initialThreadCount;
            this.finalThreadCount = finalThreadCount;
            this.memoryUsageMB = memoryUsageMB;
            this.initialThreadStates = initialThreadStates;
            this.finalThreadStates = finalThreadStates;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Wyniki wydajności:\n");
            sb.append("- CPU Time: ").append(cpuTimeMs).append(" ms\n");
            sb.append("- User Time: ").append(userTimeMs).append(" ms\n");
            sb.append("- System Time: ").append(systemTimeMs).append(" ms\n");
            sb.append("- Początkowa liczba wątków: ").append(initialThreadCount).append("\n");
            sb.append("- Końcowa liczba wątków: ").append(finalThreadCount).append("\n");
            sb.append("- Zmiana liczby wątków: ").append(finalThreadCount - initialThreadCount).append("\n");
            sb.append("- Zużycie pamięci: ").append(String.format("%.2f", memoryUsageMB)).append(" MB\n");

            sb.append("- Analiza stanów wątków:\n");
            int newThreads = 0;
            int completedThreads = 0;

            for (ThreadState finalState : finalThreadStates) {
                boolean existed = false;
                for (ThreadState initialState : initialThreadStates) {
                    if (finalState.threadId == initialState.threadId) {
                        existed = true;
                        break;
                    }
                }
                if (!existed) {
                    newThreads++;
                }
            }

            for (ThreadState initialState : initialThreadStates) {
                boolean stillExists = false;
                for (ThreadState finalState : finalThreadStates) {
                    if (initialState.threadId == finalState.threadId) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists) {
                    completedThreads++;
                }
            }

            sb.append("  - Nowe wątki: ").append(newThreads).append("\n");
            sb.append("  - Zakończone wątki: ").append(completedThreads).append("\n");

            return sb.toString();
        }
    }

    public static class ThreadState {
        private final long threadId;

        public ThreadState(long threadId) {
            this.threadId = threadId;
        }
    }
}