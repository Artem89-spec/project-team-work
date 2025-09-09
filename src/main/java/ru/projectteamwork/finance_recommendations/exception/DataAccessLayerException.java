package ru.projectteamwork.finance_recommendations.exception;

public class DataAccessLayerException extends RuntimeException {
    public DataAccessLayerException(String message,Throwable throwable) {
        super(message, throwable);
    }
}

