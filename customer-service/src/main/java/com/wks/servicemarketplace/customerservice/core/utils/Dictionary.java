package com.wks.servicemarketplace.customerservice.core.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class Dictionary<K> {

    private Map<K, Object> map;

    private Dictionary(Map<K, Object> map) {
        this.map = Optional.ofNullable(map)
                .map(Collections::unmodifiableMap)
                .orElseGet(Collections::emptyMap);
    }

    public static <K> Dictionary<K> of(Map<K, Object> map) {
        return new Dictionary<K>(map);
    }

    public <T> T get(String field, T defaultValue) {
        return (T) Optional.ofNullable(get(field))
                .orElse(defaultValue);
    }

    public <T> T get(String field){
        return (T) map.get(field);
    }
}
