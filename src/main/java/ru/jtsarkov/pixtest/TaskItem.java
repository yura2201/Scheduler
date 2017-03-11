package ru.jtsarkov.pixtest;

import java.time.LocalDateTime;

/**
 *
 * @author jtsarkov
 */
public interface TaskItem extends Comparable<TaskItem> {

    LocalDateTime getDateTime();

    Runnable getTask();
}
