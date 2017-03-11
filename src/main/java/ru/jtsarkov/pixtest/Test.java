package ru.jtsarkov.pixtest;

import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import java.time.ZoneOffset;
import java.util.Random;

/**
 *
 * @author jtsarkov
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        TaskItem itm = new TaskItemImpl(now());
        TaskItem itm2 = new TaskItemImpl(now().minusDays(1));
        TaskItem itm3 = new TaskItemImpl(now().minusDays(2));
//можно запускать как внешний сервис
//        ScheduledExecutorService srv = Executors.newSingleThreadScheduledExecutor();
        //также можно запустить внутренний Executor
        Scheduler sch = new SchedulerImpl(true);
        sch.put(itm);
        sch.put(itm2);
        sch.put(itm3);

        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int minDay = (int) LocalDateTime.of(2000, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
            int maxDay = (int) LocalDateTime.of(2017, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
            long randomDay = minDay + random.nextInt(Math.abs(maxDay - minDay));
            LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
            sch.put(new TaskItemImpl(randomDate));
        }
//        Thread.sleep(1000);
        sch.stopInternalScheduler();

//        srv.scheduleWithFixedDelay(sch, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
