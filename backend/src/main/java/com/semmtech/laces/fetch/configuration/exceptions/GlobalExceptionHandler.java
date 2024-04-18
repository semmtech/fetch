package com.semmtech.laces.fetch.configuration.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.QueryParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage configurationAlreadyExists(CodedException e) {
        return getErrorMessage(e);
    }

    private ErrorMessage getErrorMessage(CodedException e) {
        log.error(e.codedMessage(), e);
        return e.toErrorMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage invalidRequestParameter(IllegalArgumentException e) {
        return new ErrorMessage("Invalid request parameter supplied.", e.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage clientError(HttpClientErrorException e) {
        return new ErrorMessage("Request to backend system failed.", e.getResponseBodyAsString());
    }

    @ExceptionHandler(QueryParseException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage sparqlParsingError(QueryParseException e) {
        return new ErrorMessage("Unparseable query: ", e.getMessage());
    }

    @ExceptionHandler(WorkspaceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage workspaceNotFound(WorkspaceNotFoundException e) {
        return new ErrorMessage("Relatics configuration error", "We failed to read data from or write data to Relatics, please configure a Relatics Workspace.");
    }

    @ExceptionHandler(WorkspaceIdAlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage workspaceNotFound(WorkspaceIdAlreadyExistException e) {
        return new ErrorMessage(
                "Workspace already exists", "Workspace with id " + e.getMessage() + " already exists.");
    }

    @ExceptionHandler(NoEnvironmentConfiguredException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage environmentNotCorrectlyConfigured(NoEnvironmentConfiguredException e) {
        return new ErrorMessage("Relatics configuration error", "We failed to read data from or write data to Relatics, please configure a Relatics Environment.");
    }

    @ExceptionHandler(JsonApiNotFoundException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage jsonApiNotCorrectlyConfigured(JsonApiNotFoundException e) {
        return new ErrorMessage("Data target configuration error", "We failed to read data from or write data to the target data service, please correct the data service configuration.");
    }

    @ExceptionHandler(JsonApiEndpointNotFoundException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage jsonApiEndpointNotCorrectlyConfigured(JsonApiEndpointNotFoundException e) {
        return new ErrorMessage("Data target configuration error", "We failed to read data from or write data to the target data service, please correct the data service configuration.");
    }

    @ExceptionHandler(UnsupportedDeleteException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage unsupportedDeleteError(UnsupportedDeleteException e) {
        return new ErrorMessage("The requested object cannot be deleted.", e.getMessage());
    }

    @ExceptionHandler(QueryUpdateException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage queryUpdateError(QueryUpdateException qe) {
        return new ErrorMessage("Failed to update query or columns", qe.getMessage());
    }
}