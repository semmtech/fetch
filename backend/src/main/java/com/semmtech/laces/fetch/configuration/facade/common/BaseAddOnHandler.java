package com.semmtech.laces.fetch.configuration.facade.common;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class BaseAddOnHandler {
    protected final GenericService<AddOnEntity> addOnConfigurationGenericService;

    public BaseAddOnHandler(GenericService<AddOnEntity> addOnConfigurationGenericService) {
        this.addOnConfigurationGenericService = addOnConfigurationGenericService;
    }

    protected <T, U extends Identifiable> T getEntityById(
            AddOnEntity configurationEntity,
            Function<AddOnEntity, String> idExtractor,
            Predicate<AddOnEntity> areRequiredFieldsPresent,
            GenericService<U> genericService,
            Function<U, T> createDto) {

        if (areRequiredFieldsPresent.test(configurationEntity)) {
            return genericService
                    .get(idExtractor.apply(configurationEntity))
                    .map(createDto)
                    .orElse(null);
        }
        return null;
    }

    protected <T extends Identifiable, U extends Identifiable> void saveAspectAndUpdateDtoWithId(
            final GenericService<T> serviceToUse,
            final Supplier<U> aspectSupplier,
            final Function<U, T> aspectEntityProvider) {

        final var aspectToSave = aspectSupplier.get();
        if (aspectToSave != null) {
                if (StringUtils.isEmpty(aspectToSave.getId())) {
                    T savedObject = serviceToUse.create(aspectEntityProvider.apply(aspectToSave));
                    aspectToSave.setId(savedObject.getId());
                } else {
                    serviceToUse.update(aspectEntityProvider.apply(aspectToSave));
                }
        }
    }
}
