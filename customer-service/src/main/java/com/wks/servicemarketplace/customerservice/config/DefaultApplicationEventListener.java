package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.customerservice.config.schedulers.Schedulers;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.inject.Inject;

public class DefaultApplicationEventListener implements ApplicationEventListener {

    private final Schedulers schedulers;

    @Inject
    public DefaultApplicationEventListener(Schedulers schedulers) {
        this.schedulers = schedulers;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
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
