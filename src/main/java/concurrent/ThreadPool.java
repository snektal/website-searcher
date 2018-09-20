package concurrent;

import exception.ThreadpoolException;
import utils.config.ApplicationConfig;
import utils.logger.ApplicationLogger;

import java.text.MessageFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class ThreadPool {

    private static final int POOL_SIZE;
    private static final int QUEUE_CAPACITY;
    private static final long AWAIT_TERMINATION_SLEEP_TIME;
    private static final long THREAD_SLEEP_TIME;

    private final BlockingQueue<Runnable> queue;
    private final WorkerThread[] workerThreads;
    private final AtomicLong lastDumpMillis;

    static {
        POOL_SIZE = ApplicationConfig.getIntegerProperty("pool.size", 20);
        QUEUE_CAPACITY = ApplicationConfig.getIntegerProperty("pool.queue.capacity", 50);
        AWAIT_TERMINATION_SLEEP_TIME = ApplicationConfig.getLongProperty("pool.await.termination.sleep.time", 60000L);
        THREAD_SLEEP_TIME = ApplicationConfig.getLongProperty("pool.worker.thread.sleep.time", 1);
    }

    private ThreadPool() {
        this.queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.workerThreads = new WorkerThread[POOL_SIZE];

        for (int i = 0; i < POOL_SIZE; i++) {
            this.workerThreads[i] = new WorkerThread("Thread-" + i);
            this.workerThreads[i].start();
        }
        this.lastDumpMillis = new AtomicLong(System.currentTimeMillis());
    }

    private static class SingletonHolder {
        static final ThreadPool INSTANCE = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void add(Runnable task) {
        try {
            queue.put(task);
            dumpCapacity();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadpoolException(e);
        }
    }

    public void awaitTermination() {
        ApplicationLogger.log(Level.INFO, "Awaiting Termination: Started");
        while (true) {
            boolean flag = true;
            for (Thread thread : workerThreads) {
                if (thread.isAlive()) {
                    flag = false;
                    break;
                }
            }

            try {
                Thread.sleep(AWAIT_TERMINATION_SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ThreadpoolException(e);
            }

            if (flag || queue.isEmpty()) {
                ApplicationLogger.log(Level.INFO, "Awaiting Termination: Completed");
                return;
            }
        }
    }

    private void dumpCapacity() {
        try {
            long lastValue = lastDumpMillis.get();
            if (lastValue + 10000L < System.currentTimeMillis() &&
                    lastDumpMillis.compareAndSet(lastValue, System.currentTimeMillis())) {

                int size = queue.size();
                long percentsFull = Math.round((double) size / QUEUE_CAPACITY * 100);

                ApplicationLogger.log(Level.FINE, "Queue size: {0}/{1} = {2}%", size, QUEUE_CAPACITY, percentsFull);
            }
        } catch (Exception e) {
            ApplicationLogger.log(Level.SEVERE, "Error in dump capacity -> {0}", e);
        }
    }

    private class WorkerThread extends Thread {
        WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Runnable task;
            while (true) {
                try {
                    ApplicationLogger.log(Level.FINE, "Processing thread {0}", getName());
                    task = queue.take();
                    task.run();

                    Thread.sleep(THREAD_SLEEP_TIME);
                    dumpCapacity();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    throw new ThreadpoolException(MessageFormat.format("Failed to run thread {0}, Exception -> {1}", getName(), e));
                }
            }
        }
    }
}
