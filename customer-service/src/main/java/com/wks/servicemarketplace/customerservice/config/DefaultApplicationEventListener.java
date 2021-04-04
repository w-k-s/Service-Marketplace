package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.customerservice.config.schedulers.Schedulers;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DefaultApplicationEventListener implements ApplicationEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(DefaultApplicationEventListener.class);
    private final Schedulers schedulers;
    private final DatabaseMigration migration;

    @Inject
    public DefaultApplicationEventListener(
            DatabaseMigration migrations,
            Schedulers schedulers
    ) {
        this.migration = migrations;
        this.schedulers = schedulers;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_START:
                this.migration.migrate()
                        .mapErr(e -> {
                            LOGGER.error(e.toString());
                            return e;
                        })
                        .expect("Migration Failed");
                break;
            case INITIALIZATION_FINISHED:
                this.schedulers.start();
                break;
            case DESTROY_FINISHED:
                this.schedulers.shutdown();
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}
