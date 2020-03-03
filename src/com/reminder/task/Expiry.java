package com.reminder.task;

import java.util.Timer;
import java.util.TimerTask;

public class Expiry {

    private final Timer t = new Timer();

    public long startTime;

    public TimerTask schedule(final Runnable r) {
        startTime = System.currentTimeMillis();

        TimerTask task = new TimerTask() {
            public void run() {
                r.run();

            }
        };
        t.scheduleAtFixedRate(task, 1000, 1);//;
        return task;
    }
}
