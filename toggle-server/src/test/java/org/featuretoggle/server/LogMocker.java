package org.featuretoggle.server;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.mockito.Mockito;
import org.slf4j.Logger;

import lombok.SneakyThrows;

public final class LogMocker {

    private LogMocker() {
    }

    @SneakyThrows
    public static Logger getMockLoggerForClass(final Class<?> type) {
        final Field logField = type.getDeclaredField("log");
        logField.setAccessible(true);
        makeNotFinal(logField);
        Logger log = (Logger) logField.get(null);
        if (!Mockito.mockingDetails(log).isMock()) {
            log = mock(Logger.class);
            setStaticFinalField(null, "log", log, type);
        } else {
            Mockito.reset(log);
        }
        logField.setAccessible(false);
        return log;
    }

    public static void makeNotFinal(final Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            try {
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new ReflectionException(e, "Failed to make field [%s] not final", field.getName());
            }
        }
    }

    public static void setStaticFinalField(final Object obj, final String fieldName, final Object newValue, final Class<?> type) {
        try {
            final Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            makeNotFinal(field);
            field.set(obj, newValue);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new ReflectionException(e,
                    "Failed to set static final field: [fieldName=%s, newValue=%s, obj=%s, type=%s]",
                    fieldName, newValue, obj, type.getName());
        }
    }

    /**
     * Get a mocked Logger with name 'log' for an object. If the logger already exists as a mock, it
     * is reset.<br/>
     * <b>Important:</b> Make sure you get the logger for the class before you call whatever method
     * you want to verify the log for.
     */
    public static Logger getMockLoggerForObject(final Object obj) {
        return getMockLoggerForClass(obj.getClass());
    }

    /**
     * Get a mocked Logger with name 'log' for an object's super class. If the logger already exists
     * as a mock, it is reset.<br/>
     * <b>Important:</b> Make sure you get the logger for the class before you call whatever method
     * you want to verify the log for.
     */
    public static Logger getMockLoggerForSuperClass(final Object obj) {
        return getMockLoggerForClass(obj.getClass().getSuperclass());
    }
}
