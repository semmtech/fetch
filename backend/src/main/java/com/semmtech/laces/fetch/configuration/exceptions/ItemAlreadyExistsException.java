package com.semmtech.laces.fetch.configuration.exceptions;

import com.semmtech.laces.fetch.configuration.entities.Identifiable;

import java.util.List;
import java.util.stream.Collectors;

public class ItemAlreadyExistsException extends CodedException {

    public ItemAlreadyExistsException(Identifiable configuration) {
        super("Create failed, configuration with id " + configuration.getId() + " already exists.", "[CONFIG ALREADY EXISTS]");
    }

    public ItemAlreadyExistsException(List<? extends Identifiable> configurations) {
        super("Create failed, configurations with id " +
                configurations.stream()
                        .map(Identifiable::getId)
                        .collect(Collectors.joining(",")) +
                " already exist.", "[CONFIG ALREADY EXISTS]");
    }
}
