package ru.jtsarkov.pixtest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtsarkov
 */
public class SchedulerImpl implements Scheduler, Runnable {

    private final PriorityBlockingQueue<TaskItem> pbQueue = new PriorityBlockingQueue<>(1);

    private final ScheduledExecutorService srv = Executors.newSingleThreadScheduledExecutor();

    private final long delay = 1000;

    private boolean stop = true;

    private boolean isInternalSchedulerStarted = false;

    private String getStackTraceStr(Throwable tr) {
        StringWriter stack = new StringWriter();
        tr.printStackTrace(new PrintWriter(stack));
        return stack.toString();
    }

    private void doWork() throws InterruptedException {
        List<TaskItem> toDo = new LinkedList<>();
        LocalDateTime currentDt = now();
        //забираем из очереди все задания, дата выполнения которых <= currentDate
        while (pbQueue.peek() != null
                && (pbQueue.peek().getDateTime().isBefore(currentDt)
                || pbQueue.peek().getDateTime().isEqual(currentDt))) {
            toDo.add(pbQueue.take());
        }
        toDo.forEach((item) -> {
            srv.execute(item.getTask());
        });
    }

    public SchedulerImpl() {
        this(false);
    }

    public SchedulerImpl(boolean runWithInternalScheduler) {
        if (runWithInternalScheduler) {
            isInternalSchedulerStarted = true;
            srv.scheduleWithFixedDelay(() -> {
                if (stop) {
                    try {
                        stop = false;
                        doWork();
                        stop = true;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SchedulerImpl.class.getName()).log(
                                Level.SEVERE, "Interrupted by user: {0}", getStackTraceStr(ex)
                        );
                        stop = true;
                        isInternalSchedulerStarted = false;
                    }
                }
            }, 0, delay, TimeUnit.MILLISECONDS
            );
        }
    }

    @Override
    public void stopInternalScheduler() throws InterruptedException {
        if (isInternalSchedulerStarted) {
            try {
                srv.awaitTermination(delay * pbQueue.size(), TimeUnit.MILLISECONDS);
                srv.shutdown();
                if (!srv.awaitTermination(delay * pbQueue.size(), TimeUnit.MILLISECONDS)) {
                    final List<Runnable> terminated = srv.shutdownNow();
                    Logger.getLogger(SchedulerImpl.class.getName()).log(
                            Level.SEVERE, "Terminated processes count: {0}", terminated.size()
                    );
                }
                stop = true;
                isInternalSchedulerStarted = false;
            } catch (InterruptedException ex) {
                Logger.getLogger(SchedulerImpl.class.getName()).log(
                        Level.SEVERE, "Interrupted by user: {0}", getStackTraceStr(ex)
                );
                stop = true;
                isInternalSchedulerStarted = false;
                throw ex;
            }
        }
    }

    @Override
    public void put(TaskItem item) {
        pbQueue.put(item);
    }

    @Override
    public void run() {
        if (stop
                && !isInternalSchedulerStarted) {
            try {
                stop = false;
                doWork();
                Logger.getLogger(SchedulerImpl.class.getName()).log(
                        Level.INFO, "Done"
                );
                stop = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(SchedulerImpl.class.getName()).log(
                        Level.SEVERE, "Interrupted by user: {0}", getStackTraceStr(ex)
                );
                stop = true;
            }
        }
    }
}
