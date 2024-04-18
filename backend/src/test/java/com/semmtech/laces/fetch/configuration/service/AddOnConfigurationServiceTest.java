package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.exceptions.ItemAlreadyExistsException;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.repository.AddOnConfigurationRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Unit tests for AddOnConfigurationService
 * Since getConfiguration(id) and getAllConfigurations() just delegate to the CrudRepository,
 * these methods aren't covered with unit tests. We would only be testing the mock repository.
 */
public class AddOnConfigurationServiceTest {

    private AddOnConfigurationService service;

    private AddOnConfigurationRepository repository;

    @Before
    public void setup() {
        repository = mock(AddOnConfigurationRepository.class);
        service = new AddOnConfigurationService(repository, null, null, null, null);
    }

    @Test
    public void testCreate_nonExisting_createdSuccessfully() {
        AddOnEntity configuration = AddOnEntity.builder().name("Name").build();

        when(repository.save(configuration))
                .thenAnswer(invocationOnMock
                        -> AddOnEntity
                                .builder()
                                    .id("1")
                                    .name(((AddOnEntity)invocationOnMock.getArgument(0)).getName())
                                    .build());

        AddOnEntity createdConfiguration = service.create(configuration);
        validateCreatedConfiguration(createdConfiguration);

        verify(repository, never()).existsById(anyString());
        verify(repository).save(configuration);
    }

    @Test
    public void testCreate_nonExistingWithId_createdSuccessfully() {
        AddOnEntity configuration = AddOnEntity.builder().name("Name").id("1").build();

        when(repository.existsById(configuration.getId())).thenReturn(false);
        when(repository.save(configuration))
                .thenAnswer(invocationOnMock
                        -> {
                                AddOnEntity argument = invocationOnMock.getArgument(0);
                                return AddOnEntity
                                    .builder()
                                    .id(argument.getId())
                                    .name(argument.getName())
                                    .build();
                        }
                );

        AddOnEntity createdConfiguration = service.create(configuration);
        validateCreatedConfiguration(createdConfiguration);


        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).save(configuration);
    }

    @Test(expected = ItemAlreadyExistsException.class)
    public void testCreate_existing_exceptionThrown() {
        AddOnEntity configuration = AddOnEntity.builder().name("Name").id("1").build();

        when(repository.existsById(configuration.getId())).thenReturn(true);

        service.create(configuration);

        verify(repository, never()).save(configuration);
    }

    @Test
    public void update_existing_successfulUpdate() {
        AddOnEntity persistedConfiguration = AddOnEntity.builder().name("Name").id("1").build();
        AddOnEntity updateConfiguration = AddOnEntity.builder().name("Name updated").id("1").build();

        when(repository.findById("1")).thenReturn(Optional.of(persistedConfiguration));
        when(repository.save(updateConfiguration)).thenReturn(updateConfiguration);


        Optional<AddOnEntity> result = service.update(updateConfiguration);

        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.get(), equalTo(updateConfiguration));

        verify(repository, times(1)).findById("1");
        verify(repository, times(1)).save(updateConfiguration);
    }

    @Test
    public void update_nonExisting_emptyOptionalReturned() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        AddOnEntity updateConfiguration = AddOnEntity.builder().name("Name updated").id("1").build();
        Optional<AddOnEntity> result = service.update(updateConfiguration);

        assertThat(result.isPresent(), equalTo(false));

        verify(repository, times(1)).findById("1");
        verify(repository, never()).save(any(AddOnEntity.class));
    }

    @Test
    public void delete_existing_successfulDelete() {
        AddOnEntity configuration = AddOnEntity.builder().name("Name").id("1").build();

        when(repository.existsById("1")).thenReturn(true);
        List<String> result = service.delete(List.of(configuration));

        assertThat(result, is(not(empty())));
        assertThat(result.get(0), equalTo("1"));

        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).delete(configuration);
    }

    @Test
    public void delete_nonExisting_emptyListReturned() {
        AddOnEntity configuration = AddOnEntity.builder().name("Name").id("1").build();

        when(repository.existsById("1")).thenReturn(false);
        List<String> result = service.delete(List.of(configuration));

        assertThat(result, is(empty()));

        verify(repository, times(1)).existsById("1");
        verify(repository, never()).delete(configuration);
    }

    private void validateCreatedConfiguration(AddOnEntity createdConfiguration) {
        assertThat(
                createdConfiguration,
                allOf(
                        hasProperty("id", equalTo("1")),
                        hasProperty("name", equalTo("Name"))
                )
        );
    }
}
