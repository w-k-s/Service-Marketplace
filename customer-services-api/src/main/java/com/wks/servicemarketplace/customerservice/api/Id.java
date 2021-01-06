package com.wks.servicemarketplace.customerservice.api;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Id<T> {

    T id;

    public Id(T id) {
        this.id = id;
    }

    @JsonValue
    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id<?> id1 = (Id<?>) o;
        return Objects.equals(id, id1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
