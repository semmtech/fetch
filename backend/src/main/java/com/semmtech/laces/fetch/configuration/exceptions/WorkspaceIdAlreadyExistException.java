package com.semmtech.laces.fetch.configuration.exceptions;

public class WorkspaceIdAlreadyExistException extends RuntimeException {
    public WorkspaceIdAlreadyExistException(String workspaceId) {
        super(workspaceId);
    }
}
