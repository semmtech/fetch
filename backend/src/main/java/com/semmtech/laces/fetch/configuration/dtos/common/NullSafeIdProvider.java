package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.Identifiable;

public class NullSafeIdProvider {
    public static <T extends Identifiable> String getId(T identifiable) {
        return identifiable != null ? identifiable.getId() : null;
    }
}
