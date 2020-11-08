package com.wks.servicemarketplace.customerservice.core.models;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.customerservice.core.models.events.DomainEvent;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class ResultWithEvents<T, E extends DomainEvent> {
    private final T result;
    private final List<E> events;

    private ResultWithEvents(T result, List<E> events) {
        Preconditions.checkNotNull(result);
        Preconditions.checkNotNull(events);
        Preconditions.checkState(!events.isEmpty(), "events must not be empty");

        this.result = result;
        this.events = events;
    }

    public static <T, E extends DomainEvent> ResultWithEvents<T, E> of(T result, List<E> events) {
        return new ResultWithEvents<>(result, events);
    }

    public E firstEvent() {
        return this.events.get(0);
    }

    public Optional<E> getEvent(int index) {
        if (index >= this.events.size()) return Optional.empty();
        return Optional.of(this.events.get(index));
    }
}
