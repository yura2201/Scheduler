package ru.jtsarkov.pixtest;

/**
 *
 * @author jtsarkov
 */
public interface Scheduler {

    void put(TaskItem item);

    void stopInternalScheduler() throws InterruptedException;
}
