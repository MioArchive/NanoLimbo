package ua.nanit.limbo.server.command;

import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public class MemoryCommand implements Command {
    private static final long BYTES_IN_MB = 1024 * 1024;
    private static final int PERCENTAGE_MULTIPLIER = 100;
    private final LimboServer server;

    public MemoryCommand(LimboServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        MemoryInfo memoryInfo = getMemoryInfo();
        displayMemoryInfo(memoryInfo);
        checkMemoryWarnings(memoryInfo);
    }

    private MemoryInfo getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryInfo(
                (runtime.totalMemory() - runtime.freeMemory()) / BYTES_IN_MB,
                runtime.totalMemory() / BYTES_IN_MB,
                runtime.freeMemory() / BYTES_IN_MB,
                runtime.maxMemory() / BYTES_IN_MB
        );
    }

    private void displayMemoryInfo(MemoryInfo memory) {
        Log.info("Memory Usage Statistics:");
        Log.info("Used Memory: %d MB (%.1f%%)",
                memory.used,
                calculatePercentage(memory.used, memory.max));
        Log.info("Free Memory: %d MB (%.1f%%)",
                memory.free,
                calculatePercentage(memory.free, memory.max));
        Log.info("Total Allocated: %d MB (%.1f%%)",
                memory.total,
                calculatePercentage(memory.total, memory.max));
        Log.info("Maximum Available: %d MB", memory.max);
        Log.info("Garbage Collector: %s", getGarbageCollectorInfo());
    }

    private void checkMemoryWarnings(MemoryInfo memory) {
        double usedPercentage = calculatePercentage(memory.used, memory.max);
        if (usedPercentage > 90) {
            Log.error("Critical Memory Usage! Consider increasing maximum heap size!");
        } else if (usedPercentage > 80) {
            Log.error("High Memory Usage! Monitor server performance.");
        }

        if (memory.free < 100) {
            System.gc();
            Log.info("Low memory detected - Garbage collection requested");
        }
    }

    private double calculatePercentage(long value, long total) {
        return (double) value * PERCENTAGE_MULTIPLIER / total;
    }

    private String getGarbageCollectorInfo() {
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;

        for (java.lang.management.GarbageCollectorMXBean gc :
                java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            if (count >= 0) {
                totalGarbageCollections += count;
            }
            long time = gc.getCollectionTime();
            if (time >= 0) {
                garbageCollectionTime += time;
            }
        }

        return String.format("Collections: %d, Total time: %dms",
                totalGarbageCollections, garbageCollectionTime);
    }

    @Override
    public String description() {
        return "Displays detailed server memory statistics including usage, allocation and GC information";
    }

    private static class MemoryInfo {
        final long used;
        final long total;
        final long free;
        final long max;

        MemoryInfo(long used, long total, long free, long max) {
            this.used = used;
            this.total = total;
            this.free = free;
            this.max = max;
        }
    }
}
