package Manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import Utils.ReviewJob;

public class Database {
    private static Database instance;
    public ConcurrentHashMap<String, String> localToBucketMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, String> localToJobQueueMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Thread> localToThreadMap = new ConcurrentHashMap<>();
    public BlockingQueue<ReviewJob> jobsQueue = new LinkedBlockingDeque<>();
    public ConcurrentHashMap<String, ReviewJob> jobNameTojob = new ConcurrentHashMap<>();
    public ConcurrentHashMap<ReviewJob, String> jobToLocalApp = new ConcurrentHashMap<>();
    public BlockingQueue<String> workers = new LinkedBlockingDeque<>();
    public AtomicBoolean terminateLocalThreads = new AtomicBoolean(false);
    public AtomicBoolean terminateManager = new AtomicBoolean(false);
    public AtomicInteger N = new AtomicInteger(1);
    public AtomicInteger reviewsToDo = new AtomicInteger(0);

    private Database() {
    }

    synchronized public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

}

