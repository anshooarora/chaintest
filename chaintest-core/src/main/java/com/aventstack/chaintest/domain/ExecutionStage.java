package com.aventstack.chaintest.domain;

public enum ExecutionStage {

    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ERROR("Error");

    private final String _executionStage;

    ExecutionStage(final String executionStage) {
        _executionStage = executionStage;
    }

    public String getExecutionStage() {
        return _executionStage;
    }

}
