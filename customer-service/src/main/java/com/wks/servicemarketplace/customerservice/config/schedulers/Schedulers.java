package com.wks.servicemarketplace.customerservice.config.schedulers;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Schedulers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Schedulers.class);

    private List<Scheduler> schedulers;

    public Schedulers(Scheduler... schedulers) {
        this.schedulers = List.of(schedulers);
    }

    public void start() {
        schedulers.stream().filter(Objects::nonNull).forEach(scheduler -> {
            try {
                scheduler.start();
            } catch (SchedulerException e) {
                LOGGER.error("Failed to start scheduler", e);
            }
        });
    }

    public void shutdown() {
        schedulers.stream().filter(Objects::nonNull).forEach(scheduler -> {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                LOGGER.error("Failed to start scheduler", e);
            }
        });
    }
}
