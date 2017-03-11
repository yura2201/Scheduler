package ru.jtsarkov.pixtest;

import java.time.LocalDateTime;

/**
 *
 * @author jtsarkov
 */
public class TaskItemImpl implements TaskItem {

    private final LocalDateTime dateTime;
    private final Runnable task;

    public TaskItemImpl(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        task = () -> {
            //в качестве реализации будем печатать toString()
            System.out.println(this);
        };
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public Runnable getTask() {
        return task;
    }

    @Override
    public int compareTo(TaskItem o) {
        return o.getDateTime().isEqual(dateTime) ? 0 : o.getDateTime().isAfter(dateTime) ? -1 : 1;
    }

    @Override
    public String toString() {
        return "TaskItemImpl{" + "dateTime=" + dateTime + '}';
    }
}
