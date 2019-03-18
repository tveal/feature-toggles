package org.featuretoggle.server;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReflectionException(final Throwable cause, final String message, final Object... messageArgs) {
        super(createMessage(message, messageArgs), cause);
    }

    private static String createMessage(final String message, final Object[] messageArgs) {
        final List<String> stringArgs = Arrays.asList(messageArgs).stream().map(String::valueOf).collect(Collectors.toList());
        return String.format(message, stringArgs.toArray(new Object[stringArgs.size()]));
    }
}
